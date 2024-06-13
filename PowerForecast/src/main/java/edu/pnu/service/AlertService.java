package edu.pnu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pnu.DTO.AlertDTO;
import edu.pnu.DTO.RegionDTO;
import edu.pnu.domain.Alert;
import edu.pnu.domain.AlertType;
import edu.pnu.domain.Region;
import edu.pnu.persistence.AlertRepository;
import edu.pnu.persistence.RegionRepository;

@Service
public class AlertService {
	@Autowired
	private AlertRepository alertRepository;
	@Autowired
	private RegionRepository regionRepository;
	
	public List<AlertDTO> getAlertList(){
		List<Alert> alertList = alertRepository.findAll();
		List<AlertDTO> result = new ArrayList<>();
		
		for(Alert alert : alertList) {
			Region region = alert.getRegion();
			
			RegionDTO regionDTO = RegionDTO.builder()
								.sido(region.getSido())
								.gugun(region.getGugun())
								.eupmyeondong(region.getEupmyeondong())
								.build();
			AlertDTO alertDTO = AlertDTO.builder()
							.region(regionDTO)
							.alertTime(alert.getAlertTime())
							.alertType(alert.getAlertType() == AlertType.ABNORMAL ? "이상"
									: alert.getAlertType() == AlertType.INCREASE ? "상승" : "감소")
							.build();
			result.add(alertDTO);
		}
		
		return result;
	}
	
	public List<AlertDTO> getAlertRegionList(Long regionId){
		List<Alert> alertList = alertRepository.findAllByRegionRegionId(regionId);
		List<AlertDTO> result = new ArrayList<>();
		
		for(Alert alert : alertList) {
			Region region = alert.getRegion();
			
			RegionDTO regionDTO = RegionDTO.builder()
								.sido(region.getSido())
								.gugun(region.getGugun())
								.eupmyeondong(region.getEupmyeondong())
								.build();
			AlertDTO alertDTO = AlertDTO.builder()
							.region(regionDTO)
							.alertTime(alert.getAlertTime())
							.alertType(alert.getAlertType() == AlertType.ABNORMAL ? "이상"
									: alert.getAlertType() == AlertType.INCREASE ? "상승" : "감소")
							.build();
			result.add(alertDTO);
		}
		
		return result;
	}
	
	public List<AlertDTO> getAlertRegionList(String sido, String gugun, String eupmyeondong){
		Optional<Region> regionOptional = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong);

	    if (!regionOptional.isPresent()) {
	        // 지역 정보를 찾지 못한 경우 빈 리스트 반환
	        return new ArrayList<>();
	    }

	    Long regionId = regionOptional.get().getRegionId();
		List<Alert> alertList = alertRepository.findAllByRegionRegionId(regionId);
		List<AlertDTO> result = new ArrayList<>();
		
		for(Alert alert : alertList) {
			Region region = alert.getRegion();
			
			RegionDTO regionDTO = RegionDTO.builder()
								.sido(region.getSido())
								.gugun(region.getGugun())
								.eupmyeondong(region.getEupmyeondong())
								.build();
			AlertDTO alertDTO = AlertDTO.builder()
							.region(regionDTO)
							.alertTime(alert.getAlertTime())
							.alertType(alert.getAlertType() == AlertType.ABNORMAL ? "이상"
									: alert.getAlertType() == AlertType.INCREASE ? "상승" : "감소")
							.build();
			result.add(alertDTO);
		}
		
		return result;
	}
}
