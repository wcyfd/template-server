package com.randioo.template_server.common;

/**
 * 错误代码
 * 
 * @author xjd
 *
 */
public class ErrorCode {
	/** 成功 */
	public static final short SUCCESS = 1;
	/** 没有用户 */
	public static final short SHORT_TWO = 2;

	/** 拒绝登陆 */
	public static final short REJECT_LOGIN = 30111;
	/** 帐号不合法，只能是英文字母 */
	public static final short ACCOUNT_ILLEGEL = 30112;
	/** 正在登录中 */
	public static final short IN_LOGIN = 30113;
	/** 玩家名有敏感字 */
	public static final short NAME_SENSITIVE = 30114;

}
