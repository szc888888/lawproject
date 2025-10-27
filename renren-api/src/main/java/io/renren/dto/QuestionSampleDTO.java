package io.renren.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionSampleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页数")
    private int page;

    @ApiModelProperty(value = "每页条数")
    private int size;
}
