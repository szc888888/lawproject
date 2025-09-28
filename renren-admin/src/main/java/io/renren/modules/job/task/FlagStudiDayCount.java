
package io.renren.modules.job.task;

import cn.hutool.core.collection.CollUtil;
import io.renren.modules.flagstudio.entity.FlagstudioEntity;
import io.renren.modules.flagstudio.service.FlagstudioService;
import io.renren.modules.speciesList.entity.SpeciesListEntity;
import io.renren.modules.speciesList.service.SpeciesListService;
import io.renren.modules.sys.service.SysParamsService;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 会员每天更新可提问次数
 *
 * Task为spring bean的名称
 *
 * @author
 */
@Component("FlagStudiDayCount")
public class FlagStudiDayCount implements ITask{
	
	@Autowired
	private FlagstudioService flagstudioService;

	
	@Override
	public void run(String params){
		List<FlagstudioEntity> list = flagstudioService.queryAll();
		if(CollUtil.isNotEmpty(list)){
			for (FlagstudioEntity flagstudioEntity:list) {
				FlagstudioEntity flagstudio = new FlagstudioEntity();
				flagstudio.setId(flagstudioEntity.getId());
				flagstudio.setDayCount(0);
				flagstudioService.updateById(flagstudio);
			}
		}
	}
}