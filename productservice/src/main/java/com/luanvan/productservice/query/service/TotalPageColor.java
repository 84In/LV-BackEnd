package com.luanvan.productservice.query.service;

import com.luanvan.productservice.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalPageColor {

    @Autowired
    ColorRepository colorRepository;

    public Long getTotalPageColor() {
        return colorRepository.count();
    }

}
