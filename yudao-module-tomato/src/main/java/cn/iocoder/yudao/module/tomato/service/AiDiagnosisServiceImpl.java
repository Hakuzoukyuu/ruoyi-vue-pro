package cn.iocoder.yudao.module.tomato.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisDetectReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisRespVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.AiDiagnosisDO;
import cn.iocoder.yudao.module.tomato.dal.mysql.AiDiagnosisMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiDiagnosisServiceImpl implements AiDiagnosisService {

    @Value("${yudao.tomato.api-url:http://127.0.0.1:5000}")
    private String tomatoApiUrl;

    @Value("${yudao.tomato.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${yudao.tomato.deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String deepseekApiUrl;

    @Resource
    private AiDiagnosisMapper diagnosisMapper;

    private static final Map<String, String> DISEASE_MAPPING = new HashMap<>();
    static {
        DISEASE_MAPPING.put("healthy", "健康");
        DISEASE_MAPPING.put("Target_Spot", "靶斑病");
        DISEASE_MAPPING.put("YellowLeaf_Curl_Virus", "黄化曲叶病毒");
        DISEASE_MAPPING.put("Bacterial_spot", "细菌性斑点病");
        DISEASE_MAPPING.put("Early_blight", "早疫病");
        DISEASE_MAPPING.put("Late_blight", "晚疫病");
        DISEASE_MAPPING.put("Leaf_Mold", "叶霉病");
        DISEASE_MAPPING.put("Septoria_leaf_spot", "针壳孢叶斑病");
        DISEASE_MAPPING.put("Spider_mites", "蜘蛛螨");
        DISEASE_MAPPING.put("灰霉病", "灰霉病");
        DISEASE_MAPPING.put("白粉病", "白粉病");
        DISEASE_MAPPING.put("病毒病", "病毒病");
        DISEASE_MAPPING.put("蚜虫", "蚜虫");
        DISEASE_MAPPING.put("白粉虱", "白粉虱");
        DISEASE_MAPPING.put("斑潜蝇", "斑潜蝇");
    }

    @Override
    @Transactional
    public AiDiagnosisRespVO detectDisease(Long userId, MultipartFile file, AiDiagnosisDetectReqVO reqVO) {
        String detectResult = callTomatoApi(file);
        log.info("Flask API 返回结果: {}", detectResult);

        JSONObject resultJson = JSON.parseObject(detectResult);
        if (resultJson == null) {
            log.error("Flask API 返回结果无法解析为 JSON: {}", detectResult);
            throw new RuntimeException("病害识别服务返回数据格式异常");
        }

        JSONObject dataJson = resultJson.getJSONObject("data");
        if (dataJson == null) {
            log.error("Flask API 响应中缺少 data 字段: {}", detectResult);
            throw new RuntimeException("病害识别服务返回数据格式异常：缺少 data 字段");
        }

        // Flask API 返回格式: data.condition=中文病害名, data.confidence=置信度, data.condition_en=英文编码(带方括号)
        String diseaseName = dataJson.getString("condition");
        Float confidence = dataJson.getFloat("confidence");
        String conditionEn = dataJson.getString("condition_en");
        // 清理病害编码：去除方括号，如 "[Late_blight]" -> "Late_blight"
        String diseaseCode = conditionEn != null ? conditionEn.replaceAll("[\\[\\]]", "") : "";

        String weatherInfo = "";
        if (reqVO != null && reqVO.getTemperature() != null) {
            weatherInfo = String.format("温度: %.1f°C, 湿度: %.1f%%", 
                reqVO.getTemperature(), reqVO.getHumidity() != null ? reqVO.getHumidity() : 0);
        }

        String detailInfo = generateDiseaseDetail(diseaseName, weatherInfo);
        JSONObject detailJson = JSON.parseObject(detailInfo);
        
        JSONObject fullResult = new JSONObject();
        fullResult.put("diseaseName", diseaseName);
        fullResult.put("diseaseCode", diseaseCode);
        fullResult.put("confidence", confidence);
        fullResult.put("cause", detailJson.getString("cause"));
        fullResult.put("harm", detailJson.getString("harm"));
        fullResult.put("prevention", detailJson.getString("prevention"));
        fullResult.put("medicine", detailJson.getString("medicine"));
        fullResult.put("preventive", detailJson.getString("preventive"));
        fullResult.put("weatherInfo", weatherInfo);

        AiDiagnosisDO diagnosisDO = AiDiagnosisDO.builder()
                .userId(userId)
                .greenhouseId(reqVO != null ? reqVO.getGreenhouseId() : null)
                .imageUrl("/api/file/get/tomato/" + file.getOriginalFilename())
                .diseaseName(diseaseName)
                .confidence(confidence)
                .resultJson(fullResult.toJSONString())
                .createdAt(LocalDateTime.now())
                .build();

        diagnosisMapper.insert(diagnosisDO);

        AiDiagnosisRespVO respVO = BeanUtils.toBean(diagnosisDO, AiDiagnosisRespVO.class);
        respVO.setResultJsonObj(fullResult);
        return respVO;
    }

    private String callTomatoApi(MultipartFile file) {
        String url = tomatoApiUrl + "/api/tomato/detect";
        
        try {
            byte[] fileBytes = file.getBytes();
            log.info("上传文件到 Flask API，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), fileBytes.length);
            
            if (fileBytes.length == 0) {
                throw new RuntimeException("上传的图片文件为空");
            }
            
            // 将 bytes 写入临时文件再上传，Hutool 的 .form("file", File) 方式最稳定
            File tempFile = File.createTempFile("upload_", file.getOriginalFilename());
            try {
                Files.write(tempFile.toPath(), fileBytes);
                
                try (HttpResponse response = HttpRequest.post(url)
                        .form("file", tempFile)
                        .execute()) {
                    String body = response.body();
                    log.info("Flask API 响应(状态码: {}): {}", response.getStatus(), body);
                    return body;
                }
            } finally {
                // 上传完成后清理临时文件
                tempFile.delete();
            }
        } catch (IOException e) {
            log.error("调用番茄病害识别API失败", e);
            throw new RuntimeException("调用番茄病害识别API失败: " + e.getMessage());
        }
    }

    private String generateDiseaseDetail(String diseaseName, String weatherInfo) {
        if (ObjectUtil.isEmpty(deepseekApiKey)) {
            return getDefaultDiseaseInfo(diseaseName);
        }

        String prompt = String.format(
            "请提供番茄%s的详细信息，包括：发病原因（与温湿度关联%s）、危害说明、防治方案、推荐用药、预防措施。请用JSON格式输出，key分别为cause、harm、prevention、medicine、preventive。",
            diseaseName, weatherInfo
        );

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(
            new JSONObject() {{
                put("role", "user");
                put("content", prompt);
            }}
        ));
        requestBody.put("temperature", 0.7);

        try (HttpResponse response = HttpRequest.post(deepseekApiUrl)
                .header("Authorization", "Bearer " + deepseekApiKey)
                .header("Content-Type", ContentType.JSON.getValue())
                .body(requestBody.toJSONString())
                .execute()) {
            String result = response.body();
            JSONObject resultJson = JSON.parseObject(result);
            return resultJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (Exception e) {
            log.warn("调用DeepSeek API失败，使用默认病害信息", e);
            return getDefaultDiseaseInfo(diseaseName);
        }
    }

    private String getDefaultDiseaseInfo(String diseaseName) {
        Map<String, String> defaultInfo = new HashMap<>();
        
        switch (diseaseName) {
            case "晚疫病":
                defaultInfo.put("cause", "由疫霉菌引起，低温高湿环境易发病，温度18-22°C、湿度90%以上时蔓延迅速");
                defaultInfo.put("harm", "危害叶片、茎和果实，导致叶片出现暗绿色水渍状斑点，严重时整株死亡");
                defaultInfo.put("prevention", "选用抗病品种，轮作倒茬，加强通风降湿，及时清除病株");
                defaultInfo.put("medicine", "可选用霜霉威盐酸盐、烯酰吗啉、嘧菌酯等药剂喷雾防治");
                defaultInfo.put("preventive", "合理密植，控制浇水，避免叶面结露，定期喷施保护性杀菌剂");
                break;
            case "早疫病":
                defaultInfo.put("cause", "由链格孢菌引起，高温高湿环境易发病，温度25-30°C时发病严重");
                defaultInfo.put("harm", "危害叶片、茎和果实，叶片出现同心轮纹状病斑，严重影响光合作用");
                defaultInfo.put("prevention", "轮作倒茬，清除病残体，加强田间管理");
                defaultInfo.put("medicine", "可选用代森锰锌、百菌清、苯醚甲环唑等药剂喷雾防治");
                defaultInfo.put("preventive", "种子消毒，合理施肥，避免植株徒长");
                break;
            case "叶霉病":
                defaultInfo.put("cause", "由黄枝孢菌引起，高温高湿环境易发病，温度20-25°C、湿度90%以上易流行");
                defaultInfo.put("harm", "主要危害叶片，叶片出现黄色斑点，背面产生灰色霉层");
                defaultInfo.put("prevention", "加强通风透光，控制湿度，合理密植");
                defaultInfo.put("medicine", "可选用甲基硫菌灵、多菌灵、嘧菌酯等药剂喷雾防治");
                defaultInfo.put("preventive", "选用抗病品种，及时摘除病叶");
                break;
            case "灰霉病":
                defaultInfo.put("cause", "由灰葡萄孢菌引起，低温高湿环境易发病，温度15-23°C、湿度90%以上易流行");
                defaultInfo.put("harm", "危害花、果实和叶片，导致落花落果，果实腐烂");
                defaultInfo.put("prevention", "控制浇水，加强通风，及时清除病残体");
                defaultInfo.put("medicine", "可选用腐霉利、异菌脲、啶酰菌胺等药剂喷雾防治");
                defaultInfo.put("preventive", "避免连作，合理密植，花期喷药保护");
                break;
            case "白粉病":
                defaultInfo.put("cause", "由白粉菌引起，高温干燥环境易发病，温度20-27°C时发病严重");
                defaultInfo.put("harm", "危害叶片，叶片表面产生白色粉状物，影响光合作用");
                defaultInfo.put("prevention", "加强通风，控制湿度，合理施肥");
                defaultInfo.put("medicine", "可选用三唑酮、腈菌唑、醚菌酯等药剂喷雾防治");
                defaultInfo.put("preventive", "选用抗病品种，及时清除病叶");
                break;
            case "病毒病":
                defaultInfo.put("cause", "由多种病毒引起，通过蚜虫、白粉虱等昆虫传播，高温干旱易发病");
                defaultInfo.put("harm", "导致植株矮化、叶片卷曲、黄化，严重影响产量");
                defaultInfo.put("prevention", "防治传毒媒介，选用抗病品种");
                defaultInfo.put("medicine", "发病初期可选用盐酸吗啉胍、氨基寡糖素等药剂喷雾");
                defaultInfo.put("preventive", "加强田间管理，及时清除病株");
                break;
            case "细菌性斑点病":
                defaultInfo.put("cause", "由细菌引起，高温高湿环境易发病，风雨传播");
                defaultInfo.put("harm", "叶片出现褐色斑点，严重时叶片干枯");
                defaultInfo.put("prevention", "种子消毒，轮作倒茬");
                defaultInfo.put("medicine", "可选用春雷霉素、氢氧化铜等药剂喷雾");
                defaultInfo.put("preventive", "避免叶片结露，及时防治虫害");
                break;
            case "靶斑病":
                defaultInfo.put("cause", "由真菌引起，高温高湿环境易发病");
                defaultInfo.put("harm", "叶片出现褐色靶状病斑");
                defaultInfo.put("prevention", "加强通风，控制湿度");
                defaultInfo.put("medicine", "可选用苯醚甲环唑、嘧菌酯等药剂");
                defaultInfo.put("preventive", "合理密植，及时清除病叶");
                break;
            case "黄化曲叶病毒":
                defaultInfo.put("cause", "由病毒引起，烟粉虱传播");
                defaultInfo.put("harm", "植株矮化，叶片黄化卷曲");
                defaultInfo.put("prevention", "防治烟粉虱");
                defaultInfo.put("medicine", "无特效药剂，以预防为主");
                defaultInfo.put("preventive", "种植抗病品种，防虫网隔离");
                break;
            case "针壳孢叶斑病":
                defaultInfo.put("cause", "由真菌引起，温暖潮湿环境易发病");
                defaultInfo.put("harm", "叶片出现圆形小斑点");
                defaultInfo.put("prevention", "清除病残体，加强通风");
                defaultInfo.put("medicine", "可选用代森锰锌、百菌清等药剂");
                defaultInfo.put("preventive", "合理密植，避免积水");
                break;
            case "蜘蛛螨":
                defaultInfo.put("cause", "高温干燥环境易爆发");
                defaultInfo.put("harm", "吸食叶片汁液，导致叶片失绿变黄");
                defaultInfo.put("prevention", "保持田间湿度，及时清除杂草");
                defaultInfo.put("medicine", "可选用阿维菌素、哒螨灵等药剂");
                defaultInfo.put("preventive", "定期检查，发现虫情及时防治");
                break;
            case "蚜虫":
                defaultInfo.put("cause", "温暖干燥环境易繁殖");
                defaultInfo.put("harm", "吸食汁液，传播病毒病");
                defaultInfo.put("prevention", "清除杂草，黄板诱杀");
                defaultInfo.put("medicine", "可选用吡虫啉、啶虫脒等药剂");
                defaultInfo.put("preventive", "生物防治，释放天敌");
                break;
            case "白粉虱":
                defaultInfo.put("cause", "温暖环境易发生");
                defaultInfo.put("harm", "吸食汁液，分泌蜜露引发煤污病");
                defaultInfo.put("prevention", "黄板诱杀，防虫网隔离");
                defaultInfo.put("medicine", "可选用噻虫嗪、吡蚜酮等药剂");
                defaultInfo.put("preventive", "及时清除杂草和老叶");
                break;
            case "斑潜蝇":
                defaultInfo.put("cause", "成虫产卵于叶片内");
                defaultInfo.put("harm", "幼虫潜食叶肉，形成弯曲虫道");
                defaultInfo.put("prevention", "黄板诱杀成虫");
                defaultInfo.put("medicine", "可选用阿维菌素、灭蝇胺等药剂");
                defaultInfo.put("preventive", "及时清除受害叶片");
                break;
            case "健康":
                defaultInfo.put("cause", "植株生长正常");
                defaultInfo.put("harm", "无");
                defaultInfo.put("prevention", "继续保持良好的种植管理");
                defaultInfo.put("medicine", "无需用药");
                defaultInfo.put("preventive", "定期巡查，预防病虫害");
                break;
            default:
                defaultInfo.put("cause", "未知病害");
                defaultInfo.put("harm", "需要进一步诊断");
                defaultInfo.put("prevention", "建议咨询专业农技人员");
                defaultInfo.put("medicine", "请先确诊病害类型");
                defaultInfo.put("preventive", "加强田间观察");
        }
        
        return JSON.toJSONString(defaultInfo);
    }

    @Override
    public List<AiDiagnosisRespVO> getDiagnosisHistory(Long userId) {
        List<AiDiagnosisDO> list = diagnosisMapper.selectListByUserId(userId);
        return list.stream().map(this::convertToRespVO).toList();
    }

    @Override
    public AiDiagnosisRespVO getDiagnosisRecord(Long userId, Long id) {
        AiDiagnosisDO diagnosisDO = diagnosisMapper.selectById(id);
        if (diagnosisDO == null || !ObjectUtil.equal(userId, diagnosisDO.getUserId())) {
            return null;
        }
        return convertToRespVO(diagnosisDO);
    }

    @Override
    @Transactional
    public void deleteDiagnosisRecord(Long userId, Long id) {
        AiDiagnosisDO diagnosisDO = diagnosisMapper.selectById(id);
        if (diagnosisDO != null && ObjectUtil.equal(userId, diagnosisDO.getUserId())) {
            diagnosisMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void updateHelpful(Long userId, Long id, Integer isHelpful) {
        AiDiagnosisDO diagnosisDO = diagnosisMapper.selectById(id);
        if (diagnosisDO != null && ObjectUtil.equal(userId, diagnosisDO.getUserId())) {
            diagnosisDO.setIsHelpful(isHelpful);
            diagnosisMapper.updateById(diagnosisDO);
        }
    }

    @Override
    public List<AiDiagnosisRespVO> getDiagnosisByGreenhouse(Long greenhouseId) {
        List<AiDiagnosisDO> list = diagnosisMapper.selectListByGreenhouseId(greenhouseId);
        return list.stream().map(this::convertToRespVO).toList();
    }

    private AiDiagnosisRespVO convertToRespVO(AiDiagnosisDO diagnosisDO) {
        AiDiagnosisRespVO respVO = BeanUtils.toBean(diagnosisDO, AiDiagnosisRespVO.class);
        if (diagnosisDO.getResultJson() != null) {
            respVO.setResultJsonObj(JSON.parseObject(diagnosisDO.getResultJson()));
        }
        return respVO;
    }
}
