package edu.pnu.DTO.Response;

import java.util.List;

import lombok.Data;

@Data
public class WeatherItemsDTO
{
    private List<WeatherItemDTO> item;

    private int numOfRows;

    private int pageNo;

    private int totalCount;
}