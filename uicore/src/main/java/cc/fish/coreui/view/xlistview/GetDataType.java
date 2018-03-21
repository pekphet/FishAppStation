package cc.fish.coreui.view.xlistview;

/**
 * 主要用于判断获取数据的方式
 */
public class GetDataType {
	/**
	 * 第一次进入界面是获取数据
	 */
	public static final int FIRST_GET_DATA = 1;
	/**
	 * ListView,GridView等可以刷新的view下拉刷新获取数据
	 */
	public static final int REFRESH_GET_DATA = 2;
	/**
	 * ListView,GridView等可以上拉加载的view上拉加载获取数据
	 */
	public static final int LOAD_GET_DATA = 3;
}
