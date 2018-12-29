package com.xinge.pien.controller.system;

import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.auto.SysUser;
import com.xinge.pien.domain.extend.ExtSysUserAndRole;
import com.xinge.pien.service.system.SysUserService;
import com.xinge.pien.service.system.SysUserTokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SysLoginController extends BaseController {

	@Value("${pien.defaultServiceNum}")
	private int pienDefaultServiceNum;

	@Value("${pien.defaultUserPassword}")
	private String pienDefaultUserPassword;


	@Autowired
	private SysUserService userService;

	@Autowired
	private SysUserTokenService userTokenService;


	/**
	 * 登录
	 */
	@PostMapping("sysLogin/login")
	@ResponseBody
	public ResponseBo login(String username, String password) {
		//用户信息
		SysUser conUser = new SysUser();
		conUser.setUsername(username);
		conUser.setStatus("1");
		SysUser user = userService.getByModel(conUser);

		//账号不存在、密码错误
		if(user == null || !password.equals(user.getPassword())) {
			return ResponseBo.warn("账号或密码不正确");
		} else {
			return userTokenService.createToken(user.getPid());
		}
	}

	/**
	 * 登出
	 */
	@GetMapping("sysLogin/logout")
	@ResponseBody
	public ResponseBo logout() {
		userTokenService.logout(getUserPid());
		return ResponseBo.ok();
	}


	/**
	 * 注册
	 */
	@PostMapping("sysLogin/register")
	@ResponseBody
	public ResponseBo register(SysUser record) {
		try {
			//校验账号
			Boolean checkFlag = false;
			if (StringUtils.isNotBlank(record.getUsername())) {
				SysUser checkModel = new SysUser();
				checkModel.setUsername(record.getUsername());
				SysUser result = userService.checkByModel(record);
				checkFlag = (result == null);
			}
			if(!checkFlag) {
				return ResponseBo.warn("注册用户的账号重复");
			}

			//用户及默认角色
			ExtSysUserAndRole userAndRole = new ExtSysUserAndRole();
			userAndRole.setUsername(record.getUsername());
			userAndRole.setPassword(record.getPassword());
			userAndRole.setContactPhone(record.getContactPhone());
			userAndRole.setServiceNum(pienDefaultServiceNum);
			userAndRole.setServiceUseNum(0);
			userAndRole.setStatus("1");
			List<Long> listPids = new ArrayList<>();
			listPids.add(3L);
			userAndRole.setRolePids(listPids);
			userService.addUserAndRole(userAndRole);
			return ResponseBo.ok("注册成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("注册失败，请联系微信客服");
		}
	}

	/**
	 * 忘记密码
	 */
	@PostMapping("sysLogin/forgot")
	@ResponseBody
	public ResponseBo forgot(SysUser record) {
		try {
			SysUser user = userService.getByModel(record);
			if (user == null) {
				return ResponseBo.warn("无对应账号信息，请确认输入的信息");
			} else {
				SysUser userPassword = new SysUser();
				userPassword.setPid(user.getPid());
				userPassword.setPassword(pienDefaultUserPassword);
				userService.updateNotNull(userPassword);
				return ResponseBo.ok("账号密码已重置为123456<br/>安全起见，登录后请尽快修改密码", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取失败，请联系微信客服");
		}
	}



}
