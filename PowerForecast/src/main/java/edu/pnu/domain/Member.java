package edu.pnu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	@Column(nullable = false)
	private String nickname;
	@Column(nullable = false, unique = true)
	private String id;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String phoneNumber;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
    @Column(nullable = false)
	private Role role = Role.USER;
	
	@ManyToOne
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;
}
