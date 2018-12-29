package com.xinge.pien.controller.system;

import com.xinge.pien.common.annotation.Log;
import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.common.domain.Tree;
import com.xinge.pien.domain.auto.SysMenu;
import com.xinge.pien.service.system.SysMenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SysMenuController extends BaseController {

	@Autowired
	private SysMenuService menuService;


	/**
	 * 获取菜单列表
	 */
	@RequestMapping("sysMenu/getList")
	@ResponseBody
	public ResponseBo getList(SysMenu record) {
		try {
			List<SysMenu> list = menuService.getList(record);
			return ResponseBo.ok(null, list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取菜单列表失败");
		}
	}


	/**
	 * 获取菜单树
	 */
	@RequestMapping("sysMenu/getTree")
	@ResponseBody
	public ResponseBo getTree() {
		try {
			Tree<SysMenu> tree = menuService.getTreeAll();
			return ResponseBo.ok(null, tree.getChildren());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取菜单树失败");
		}
	}

	/**
	 * 新增菜单/按钮
	 */
	@Log("新增菜单/按钮")
	@RequiresPermissions("sysMenu:add")
	@RequestMapping("sysMenu/add")
	@ResponseBody
	public ResponseBo add(SysMenu record) {
		String name;
		if ("0".equals(record.getType()))
			name = "菜单";
		else
			name = "按钮";
		try {
			//校验菜单名称
			SysMenu checkModel = new SysMenu();
			checkModel.setName(record.getName());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("新增菜单的名称重复");
			}

			menuService.add(record);
			return ResponseBo.ok("新增" + name + "成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增" + name + "失败，请联系微信客服");
		}
	}


	/**
	 * 据主键获取菜单信息
	 */
	@RequestMapping("sysMenu/getByPid")
	@ResponseBody
	public ResponseBo getByPid(Long pid) {
		try {
			SysMenu menu = this.menuService.getByPid(pid);
			return ResponseBo.ok(null, menu);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取失败，请联系微信客服");
		}
	}

	/**
	 * 修改菜单/按钮
	 */
	@Log("修改菜单/按钮")
	@RequiresPermissions("sysMenu:edit")
	@RequestMapping("sysMenu/edit")
	@ResponseBody
	public ResponseBo edit(SysMenu record) {
		String name;
		if ("0".equals(record.getType()))
			name = "菜单";
		else
			name = "按钮";
		try {
			//校验菜单名称
			SysMenu checkModel = new SysMenu();
			checkModel.setName(record.getName());
			checkModel.setPid(record.getPid());
			if(!checkName(checkModel)) {
				return ResponseBo.warn("修改菜单的名称重复");
			}

			menuService.edit(record);
			return ResponseBo.ok("修改" + name + "成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改" + name + "失败，请联系微信客服");
		}
	}


	/**
	 * 删除菜单
	 */
	@Log("删除菜单")
	@RequiresPermissions("sysMenu:delete")
	@RequestMapping("sysMenu/delete")
	@ResponseBody
	public ResponseBo delete(String ids) {
		try {
			menuService.deleteByPids(ids);
			return ResponseBo.ok("删除成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除失败，请联系微信客服！");
		}
	}


	/**
	 * 校验菜单名称
	 */
	@RequestMapping("sysMenu/checkName")
	@ResponseBody
	public boolean checkName(SysMenu record) {
		Boolean checkFlag = false;
		if (StringUtils.isNotBlank(record.getName())) {
			SysMenu result = menuService.checkByModel(record);
			checkFlag = (result == null);
		}
		return checkFlag;
	}

}
