package com.ohgiraffers.hitechautoworks.res.dao;

import com.ohgiraffers.hitechautoworks.res.dto.ResDTO;
import com.ohgiraffers.hitechautoworks.res.dto.ResRegistDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResMapper {
    List<ResDTO> findAllres();

    ResDTO findUserRes(int resCode);

    List<ResDTO> findCodeRes(int resCode);

    void registres(ResRegistDTO resRegistDTO);
}
