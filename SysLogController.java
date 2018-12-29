package com.xinge.pien.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.auto.SysLog;
import com.xinge.pien.domain.auto.SysUser;
import com.xinge.pien.domain.extend.ExtSysLog;
import com.xinge.pien.service.system.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SysLogController extends BaseController {

	@Autowired
	private SysLogService sysLogService;


	/**
	 * 获取某用户系统日志列表
	 */
	@RequestMapping("sysLog/getListByUser")
	@ResponseBody
	public Map<String, Object> getListByUser(ExtSysLog record) {
		SysUser currentUser = super.getCurrentUser();
		record.setUsername(currentUser.getUsername());
		PageHelper.startPage(record.getPage(), record.getLimit());
		List<SysLog> list = sysLogService.getList(record);
		PageInfo<SysLog> pageInfo = new PageInfo<>(list);
		return getResultPage(pageInfo);
	}


	/**
	 * 获取系统日志列表
	 */
	@RequestMapping("sysLog/getList")
	@ResponseBody
	public Map<String, Object> getList(ExtSysLog record) {
		PageHelper.startPage(record.getPage(), record.getLimit());
		List<SysLog> list = sysLogService.getList(record);
		PageInfo<SysLog> pageInfo = new PageInfo<>(list);
		return getResultPage(pageInfo);
	}

	/**
	 * 删除系统日志
	 */
	@RequiresPermissions("sysLog:delete")
	@RequestMapping("sysLog/delete")
	@ResponseBody
	public ResponseBo delete(String pids) {
		try {
			sysLogService.deleteByPids(pids);
			return ResponseBo.ok("删除日志成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除日志失败，请联系微信客服！");
		}
	}
}
