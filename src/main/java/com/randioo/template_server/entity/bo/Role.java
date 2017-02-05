package com.randioo.template_server.entity.bo;

public class Role extends GameRole {

	/** 银币 */
	private int money;

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	@Override
	public boolean checkChange() {
		// TODO Auto-generated method stub
		return false;
	}

}
