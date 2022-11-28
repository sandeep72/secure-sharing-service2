package Entity;

public class Group {
	private long group_id;
	private String user_name;
	private String group_name;
	private int active;
	
	
	public Group(long group_id, String user_name, String group_name, int active) {
		super();
		this.group_id = group_id;
		this.user_name = user_name;
		this.group_name = group_name;
		this.active = active;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	
	
		
}
