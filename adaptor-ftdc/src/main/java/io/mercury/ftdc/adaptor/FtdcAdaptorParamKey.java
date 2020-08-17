package io.mercury.ftdc.adaptor;

import io.mercury.common.param.ParamType;
import io.mercury.redstone.core.adaptor.AdaptorParamKey;

/**
 * 用于读取FTDC配置信息
 * 
 * @author yellow013
 *
 */
public enum FtdcAdaptorParamKey implements AdaptorParamKey {

	TraderAddr("traderAddr", ParamType.STRING),

	MdAddr("mdAddr", ParamType.STRING),

	AppId("appId", ParamType.STRING),

	BrokerId("brokerId", ParamType.STRING),

	InvestorId("investorId", ParamType.STRING),

	AccountId("accountId", ParamType.STRING),

	UserId("userId", ParamType.STRING),

	Password("password", ParamType.STRING),

	AuthCode("authCode", ParamType.STRING),

	IpAddr("ipAddr", ParamType.STRING),

	MacAddr("macAddr", ParamType.STRING),

	CurrencyId("currencyId", ParamType.STRING),

	;

	private String kname;
	private ParamType type;

	private FtdcAdaptorParamKey(String kname, ParamType type) {
		this.kname = kname;
		this.type = type;
	}

	@Override
	public int id() {
		return ordinal();
	}

	@Override
	public String kname() {
		return kname;
	}

	@Override
	public ParamType type() {
		return type;
	}

	@Override
	public String adaptorName() {
		return "FtdcAdaptor";
	}

	public static void main(String[] args) {

		for (FtdcAdaptorParamKey key : FtdcAdaptorParamKey.values()) {
			System.out.println(key + " -> " + key.ordinal());
		}

	}

}
