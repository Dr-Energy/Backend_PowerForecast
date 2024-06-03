package edu.pnu.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Powerforecast {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "powerforecast_id")
    private Long powerForecastId;
	
	@Column(name = "predicted_power")
	private float predictedPower;

	@ManyToOne
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;
	
	@Builder.Default
	@Temporal(value = TemporalType.TIMESTAMP)	// datatype지정(이 코드에서는 timestamp)
	private Date time = new Date();
}
