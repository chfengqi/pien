package com.xinge.pien.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinge.pien.common.annotation.Log;
import com.xinge.pien.common.controller.BaseController;
import com.xinge.pien.common.domain.ResponseBo;
import com.xinge.pien.domain.auto.BisPayment;
import com.xinge.pien.domain.auto.SysUser;
import com.xinge.pien.service.business.BisPaymentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SysBuyController extends BaseController {

	@Autowired
	private BisPaymentService paymentService;


	/**
	 * 获取支付管理列表
	 */
	@RequestMapping("sysBuy/getList")
	@ResponseBody
	public Map<String, Object> getList(BisPayment record) {
		PageHelper.startPage(record.getPage(), record.getLimit());
		List<BisPayment> list = paymentService.getList(record);
		PageInfo<BisPayment> pageInfo = new PageInfo<>(list);
		return getResultPage(pageInfo);
	}


	/**
	 * 新增支付
	 */
	@Log("新增支付")
	@RequiresPermissions("sysBuy:add")
	@RequestMapping("sysBuy/add")
	@ResponseBody
	public ResponseBo add(BisPayment record) {
		try {
			SysUser currentUser = super.getCurrentUser();
			record.setCreateUserPid(currentUser.getPid());
			record.setCreateUserName(currentUser.getUsername());
			paymentService.add(record);
			return ResponseBo.ok("新增成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增失败，请联系微信客服");
		}
	}


	/**
	 * 删除支付
	 */
	@Log("删除支付")
	@RequiresPermissions("sysBuy:delete")
	@RequestMapping("sysBuy/delete")
	@ResponseBody
	public ResponseBo delete(String pids) {
		try {
			paymentService.deleteByPids(pids);
			return ResponseBo.ok("删除成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除失败，请联系微信客服");
		}
	}

}
