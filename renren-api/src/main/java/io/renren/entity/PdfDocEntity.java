package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Data
@TableName("tb_pdf_doc")
public class PdfDocEntity {

    /**
     * 主键ID
     */
	private Long id;
    /**
     * 文件名
     */
	private String fileName;
    /**
     * 类型(0是PDF 1doc)
     */
	private Integer type;
    /**
     * 用户id
     */
	private Long userId;
    /**
     * 上传时间
     */
	private Date createTime;

	public PdfDocEntity(){}
	public PdfDocEntity(Long userId,String fileName,Integer type){
	    this.userId = userId;
	    this.fileName = fileName;
	    this.type = type;
	    this.createTime = new Date();

    }
}