package io.renren.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 实体类 - 对应表 tb_question_sample
 *
 * id 会在插入时自动生成为 UUID（32 位，不含连字符）
 */
@TableName("tb_question_sample")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSampleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("parent_id")
    private String parentId;


    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("icon")
    private String icon;


    /**
     * 创建时间，自动填充（插入时）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间，自动更新（插入/更新时）
     * 注意映射到表中的列名 'udpate_time'（原样使用）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("remark")
    private String remark;

}