package com.xinge.pien.controller.system;

import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.extend.ExtSysUserOnline;
import com.xinge.pien.domain.auto.SysUser;
import com.xinge.pien.service.system.SysSessionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
public class SysSessionController extends BaseController {
	
	@Autowired
	SysSessionService sessionService;


	/**
	 * 页面在线用户
	 */
	@RequestMapping("sysSession")
	@RequiresPermissions("sysSession:list")
	public String index(Model model) {
		SysUser user = super.getCurrentUser();
		model.addAttribute("userPid", user.getPid());
		return "system/session/list";
	}

	/**
	 * 获取在线用户
	 */
	@ResponseBody
	@RequestMapping("sysSession/getList")
	public Map<String, Object> getList() {
		List<ExtSysUserOnline> list = sessionService.getList();
		return getResultList(list);
	}

	/**
	 * 强制下线
	 */
	@ResponseBody
	@RequiresPermissions("sysSession:forceLogout")
	@RequestMapping("sysSession/forceLogoutAction")
	public ResponseBo forceLogoutAction(String id) {
		try {
			//sessionService.forceLogout(id);
			return ResponseBo.ok("该用户已强制下线", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("踢出用户失败");
		}

	}
}
