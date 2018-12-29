package com.xinge.pien.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinge.pien.common.annotation.Log;
import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.auto.SysRole;
import com.xinge.pien.domain.extend.ExtSysRoleAndMenu;
import com.xinge.pien.service.system.SysMenuService;
import com.xinge.pien.service.system.SysRoleService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SysRoleController extends BaseController {

	@Autowired
	private SysRoleService roleService;

	@Autowired
	private SysMenuService menuService;


	/**
	 * 获取全部角色列表
	 */
	@RequestMapping("sysRole/getListAll")
	@ResponseBody
	public ResponseBo getListAll() {
		try {
			List<SysRole> listRole = this.roleService.getList(new SysRole());
			return ResponseBo.ok(null, listRole);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取全部角色列表失败");
		}
	}


	/**
	 * 获取角色列表
	 */
	@RequestMapping("sysRole/getList")
	@ResponseBody
	public Map<String, Object> getList(SysRole record) {
		PageHelper.startPage(record.getPage(), record.getLimit());
		List<SysRole> list = roleService.getList(record);
		PageInfo<SysRole> pageInfo = new PageInfo<>(list);
		return getResultPage(pageInfo);
	}


	/**
	 * 新增角色
	 * @param record
	 * @return
	 */
	@Log("新增角色")
	@RequiresPermissions("sysRole:add")
	@RequestMapping("sysRole/add")
	@ResponseBody
	public ResponseBo add(ExtSysRoleAndMenu record) {
		try {
			//校验账号
			SysRole checkModel = new SysRole();
			checkModel.setName(record.getName());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("新增角色的名称重复");
			}

			roleService.addRoleAndMenu(record);
			return ResponseBo.ok("新增角色成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增角色失败，请联系微信客服");
		}
	}


	/**
	 * 获取角色及菜单信息
	 */
	@RequestMapping("sysRole/getByPid")
	@ResponseBody
	public ResponseBo getByPid(Long pid) {
		try {
			ExtSysRoleAndMenu roleMenu = roleService.getRoleAndMenuByPid(pid);
			return ResponseBo.ok(null, roleMenu);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取失败，请联系微信客服");
		}
	}

	/**
	 * 修改角色
	 */
	@Log("修改角色")
	@RequiresPermissions("sysRole:edit")
	@RequestMapping("sysRole/edit")
	@ResponseBody
	public ResponseBo edit(ExtSysRoleAndMenu record) {
		try {
			//校验账号
			SysRole checkModel = new SysRole();
			checkModel.setName(record.getName());
			checkModel.setPid(record.getPid());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("修改角色的名称重复");
			}

			roleService.editRoleAndMenu(record);
			return ResponseBo.ok("修改角色成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改角色失败，请联系微信客服");
		}
	}


	/**
	 * 删除角色
	 */
	@Log("删除角色")
	@RequiresPermissions("sysRole:delete")
	@RequestMapping("sysRole/delete")
	@ResponseBody
	public ResponseBo delete(String pids) {
		try {
			roleService.deleteByPids(pids);
			return ResponseBo.ok("删除角色成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除角色失败，请联系微信客服");
		}
	}


	/**
	 * 校验角色名称
	 */
	@RequestMapping("sysRole/checkName")
	@ResponseBody
	public boolean checkName(SysRole record) {
		Boolean checkFlag = false;
		if (StringUtils.isNotBlank(record.getName())) {
			SysRole result = roleService.checkByModel(record);
			checkFlag = (result == null);
		}
		return checkFlag;
	}

}
