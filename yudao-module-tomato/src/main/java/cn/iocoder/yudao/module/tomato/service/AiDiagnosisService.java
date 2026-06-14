package cn.iocoder.yudao.module.tomato.service;

import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisDetectReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiDiagnosisService {

    AiDiagnosisRespVO detectDisease(Long userId, MultipartFile file, AiDiagnosisDetectReqVO reqVO);

    List<AiDiagnosisRespVO> getDiagnosisHistory(Long userId);

    AiDiagnosisRespVO getDiagnosisRecord(Long userId, Long id);

    void deleteDiagnosisRecord(Long userId, Long id);

    void updateHelpful(Long userId, Long id, Integer isHelpful);

    List<AiDiagnosisRespVO> getDiagnosisByGreenhouse(Long greenhouseId);

}
