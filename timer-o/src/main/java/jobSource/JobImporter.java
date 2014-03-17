package jobSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import localdb.DataManager;
import localdb.HibernateDataManager;
import localdb.Job;

public class JobImporter {

	public static void main(String[] args) throws ClassNotFoundException {
		String sql = 
				" SELECT  jobs3.codex,\n"
				+ " jobsval.jvdesc as Status,\n"
				+ "  jobs3.import as Pri ,\n"
				+ " jobs3.client ,           \n"
				+ " jobs3.descrq as Description,\n"
				+ " jobs3.DESCRP as Response,       \n"
				+ " jobs3.ECOST as EstimatedCost,       \n"
				+ " jobs3.RTCOST as Charged,       \n"
				+ " SUBSTR(jobs3.extra1,1,3) as team,\n"
				+ " SUBSTR(jobs3.extra1,4,3) as area,\n"
				+ " CONCAT(SUBSTR(jobs3.reques,1,4),CONCAT('-',CONCAT(SUBSTR(jobs3.reques,5,2),CONCAT('-',SUBSTR(jobs3.reques,7,2))))) as logged\n"
				+ " \n"
				+ " FROM  jobs3       \n"
				+ " full join jobsval on jobsval.jvcode = jobs3.status and jobsval.jvtype = 'STS'\n"
				+ " WHERE jobs3.whodo = 'NGLI'\n"
				+ " AND jobs3.status NOT IN ('C','D')\n" + "AND jobs3.jtype='J'";
		Class.forName("com.ibm.as400.access.AS400JDBCDriver");
		String url = "jdbc:as400://tracey.servers.jhc.co.uk;naming=system;prompt=false;libraries=JHCJUTIL;user=INFANTEN;password=qwopqwop";
		try (Connection connect = DriverManager.getConnection(url);) {
			 DataManager dataManager = new HibernateDataManager();
			 dataManager.init();
			 Statement statement = connect.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String reference = resultSet.getString("codex");
				String description = resultSet.getString("Description");
				Job job = dataManager.getJobByReference(reference);
				if (job == null) {
				job = new Job();
					job.setReference(reference);
				}
				job.setSource("JOB");
				job.setDescription(description);
				dataManager.save(job);
			}
			
			dataManager.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
