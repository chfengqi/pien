package com.xinge.pien.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinge.pien.common.annotation.Log;
import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.auto.BisPayment;
import com.xinge.pien.domain.auto.SysRole;
import com.xinge.pien.domain.auto.SysUser;
import com.xinge.pien.domain.extend.ExtSysRoleAndCheck;
import com.xinge.pien.domain.extend.ExtSysUserAndRole;
import com.xinge.pien.service.business.BisPaymentService;
import com.xinge.pien.service.system.SysRoleService;
import com.xinge.pien.service.system.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SysUserController extends BaseController {

	@Autowired
	private SysUserService userService;

	@Autowired
	private SysRoleService roleService;

	@Autowired
	private BisPaymentService paymentService;


	@Value("${pien.defaultServiceNum}")
	private int pienDefaultServiceNum;


	/**
	 * 获取用户列表
	 */
	@RequestMapping("sysUser/getList")
	@ResponseBody
	public Map<String, Object> getList(SysUser record) {
		PageHelper.startPage(record.getPage(), record.getLimit());
		List<SysUser> list = this.userService.getList(record);
		PageInfo<SysUser> pageInfo = new PageInfo<>(list);
		if (pageInfo.getList().size() > 0) {
		    for (SysUser item : pageInfo.getList()) {
                item.setPassword(null);
            }
        }
		return getResultPage(pageInfo);
	}


	/**
	 * 新增用户
	 */
	@Log("新增用户")
	@RequestMapping("sysUser/add")
	@RequiresPermissions("sysUser:add")
	@ResponseBody
	public ResponseBo add(ExtSysUserAndRole record) {
		try {
			//校验账号
			SysUser checkModel = new SysUser();
			checkModel.setUsername(record.getUsername());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("新增用户的账号重复");
			}

			if (StringUtils.isBlank(record.getStatus())) {
				record.setStatus("0");
			}
			record.setServiceNum(pienDefaultServiceNum);
			record.setServiceUseNum(0);
			this.userService.addUserAndRole(record);
			return ResponseBo.ok("新增用户成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增用户失败，请联系微信客服");
		}
	}


	/**
	 * 据主键获取用户信息
	 */
	@RequestMapping("sysUser/getByPid")
	@ResponseBody
	public ResponseBo getByPid(Long pid) {
		try {
			ExtSysUserAndRole userRole = this.userService.getUserAndRoleByPid(pid);

			List<SysRole> listRole = this.roleService.getList(new SysRole());
			List<ExtSysRoleAndCheck> listRoleCheck = new ArrayList<>();
			if (listRole != null && listRole.size() > 0) {
				for (SysRole role : listRole) {
					ExtSysRoleAndCheck ec = new ExtSysRoleAndCheck();
					ec.setPid(role.getPid());
					ec.setName(role.getName());
					if (userRole != null && userRole.getRolePids() != null
							&& userRole.getRolePids().size() > 0
							&& userRole.getRolePids().contains(role.getPid())) {
						ec.setIsUserChecked(true);
					} else {
						ec.setIsUserChecked(false);
					}
					listRoleCheck.add(ec);
				}
			}

			userRole.setListRoleAndCheck(listRoleCheck);

			return ResponseBo.ok(null, userRole);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取失败，请联系微信客服");
		}
	}

	/**
	 * 编辑用户
	 */
	@Log("编辑用户")
	@RequestMapping("sysUser/edit")
	@RequiresPermissions("sysUser:edit")
	@ResponseBody
	public ResponseBo edit(ExtSysUserAndRole record) {
		try {
			//校验账号
			SysUser checkModel = new SysUser();
			checkModel.setUsername(record.getUsername());
			checkModel.setPid(record.getPid());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("修改用户的账号重复");
			}

			if (StringUtils.isBlank(record.getStatus())) {
				record.setStatus("0");
			}
			this.userService.editUserAndRole(record);
			return ResponseBo.ok("修改用户成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改用户失败，请联系微信客服");
		}
	}


	/**
	 * 删除用户
	 */
	@Log("删除用户")
	@RequiresPermissions("sysUser:delete")
	@RequestMapping("sysUser/delete")
	@ResponseBody
	public ResponseBo delete(String pids) {
		try {
			userService.deleteByPids(pids);
			return ResponseBo.ok("删除成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除失败，请联系微信客服");
		}
	}


	/**
	 * 添加服务
	 */
	@Log("添加服务")
	@RequestMapping("sysUser/service")
	@RequiresPermissions("sysUser:service")
	@ResponseBody
	public ResponseBo service(BisPayment record) {
		try {
			SysUser currentUser = super.getCurrentUser();
			record.setCreateUserPid(currentUser.getPid());
			record.setCreateUserName(currentUser.getUsername());
			paymentService.add(record);
			return ResponseBo.ok("添加服务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("添加服务失败，请联系微信客服");
		}
	}



	/**
	 * 校验用户账号名称
	 */
	@RequestMapping("sysUser/checkName")
	@ResponseBody
	public boolean checkName(SysUser record) {
		Boolean checkFlag = false;
		if (StringUtils.isNotBlank(record.getUsername())) {
			SysUser result = userService.checkByModel(record);
			checkFlag = (result == null);
		}
		return checkFlag;
	}

}
