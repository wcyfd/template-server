package com.randioo.template_server.entity.bo;

import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.utils.db.Saveable;

public abstract class GameRole implements RoleInterface, Saveable {

	protected String account;
	protected String name;
	protected int roleId;

	@Override
	public String getAccount() {
		// TODO Auto-generated method stub
		return account;
	}

	@Override
	public void setAccount(String account) {
		// TODO Auto-generated method stub
		this.account = account;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public int getRoleId() {
		// TODO Auto-generated method stub
		return roleId;
	}

	@Override
	public void setRoleId(int roleId) {
		// TODO Auto-generated method stub
		this.roleId = roleId;
	}

	protected boolean change;

	@Override
	public void setChange(boolean change) {
		// TODO Auto-generated method stub
		this.change = change;
	}

	@Override
	public boolean isChange() {
		// TODO Auto-generated method stub
		if (!change) {
			change = checkChange();
		}
		return change;
	}

	@Override
	public abstract boolean checkChange();

}
