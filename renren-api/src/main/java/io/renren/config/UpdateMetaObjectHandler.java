package io.renren.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 数据库insert/update 操作 处理器
 */
@Component
class UpdateMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增数据执行
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", now(), metaObject);//updatebatch没有
//        String createBy;
//        try {
//            createBy = SecurityUtils.getUsername();
//        }catch (ServiceException e){
//            createBy = "system";
//        }
//        this.setFieldValByName("createBy", createBy, metaObject);
    }

    /**
     * 更新数据执行
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", now(), metaObject);
/*        String updateBy;
        try {
            updateBy = SecurityUtils.getUsername();
        }catch (ServiceException e){
            updateBy = "system";
        }
        this.setFieldValByName("updateBy", updateBy, metaObject);*/
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(Clock.system(ZoneId.of("Asia/Shanghai")));
    }
}
