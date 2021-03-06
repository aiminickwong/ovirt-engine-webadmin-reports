package org.reports.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.reports.Backend;
import org.reports.model.HostInterfaceDailyHistory;

public class HostInterfaceDailyHistoryDao extends BaseDao {
	private static HostInterfaceDailyHistoryDao instance;
//	public HostInterfaceDailyHistoryDao(Connection conn) throws SQLException {
//		super(conn);
//	}
	
	// 获取一个主机在某个小时内的所有网络接口 ID
	public List<UUID> queryHostInterfaceIdsByHostIdAndPeriod(String startDate, String endDate, UUID host_id) throws SQLException{
		List<UUID> hostInterfaceIdsOfOneHost = new ArrayList<UUID>();
		Statement stmt = Backend.conn.createStatement();
		ResultSet rs = null;
		rs = stmt.executeQuery("select hidh.host_interface_id from host_interface_configuration hic, host_interface_daily_history hidh"
				+ " where hic.host_interface_id = hidh.host_interface_id and"
				+ " hic.host_id = '" + host_id
				+ "' and to_char(history_datetime, 'YYYY-MM-DD') >= '" + startDate
				+ "' and to_char(history_datetime, 'YYYY-MM-DD') <= '" + endDate
				+ "' group by hidh.host_interface_id;");
		while (rs.next()) {
			hostInterfaceIdsOfOneHost.add(UUID.fromString(rs.getString("host_interface_id")));
		}
		return hostInterfaceIdsOfOneHost;
	}
	
	// 查询虚拟机在一周，一月，一季度，一年的网络传入/传出数据
	public List<HostInterfaceDailyHistory> queryNetworkRateByDays(String startDate, String endDate, UUID host_interface_id)
			throws Exception {
		Statement stmt = Backend.conn.createStatement();
		ResultSet rs = stmt.executeQuery("select to_char(history_datetime, 'YYYY-MM-DD'), receive_rate_percent, max_receive_rate_percent, transmit_rate_percent, max_transmit_rate_percent"
				+ " from (select *, row_number() over(partition by history_datetime order by history_datetime) as row_number from host_interface_daily_history where host_interface_id = '"
				+ host_interface_id + "') as rows"
			   + " where row_number = 1"
				+ " and to_char(history_datetime, 'YYYY-MM-DD') >= '" + startDate
				+ "' and to_char(history_datetime, 'YYYY-MM-DD') <= '" + endDate
				+ "';");

		List<HostInterfaceDailyHistory> lvidh = new ArrayList<HostInterfaceDailyHistory>();
		HostInterfaceDailyHistory vidh = null;
		while (rs.next()) {
			vidh = new HostInterfaceDailyHistory();
			vidh.setHistory_datetime(rs.getString("to_char"));
			vidh.setReceive_rate_percent(rs.getInt("receive_rate_percent"));
			vidh.setMax_receive_rate_percent(rs.getInt("max_receive_rate_percent"));
			vidh.setTransmit_rate_percent(rs.getInt("transmit_rate_percent"));
			vidh.setMax_transmit_rate_percent(rs.getInt("max_transmit_rate_percent"));
			vidh.setHost_interface_name(getHostInterfaceNameByHostInterfaceId(host_interface_id));
			lvidh.add(vidh);
		}
		return lvidh;
	}
	
	public static HostInterfaceDailyHistoryDao getInstance() throws SQLException {
        if (instance == null) {
            instance = new HostInterfaceDailyHistoryDao();
            return instance;
        }
        return instance;
    }
}
