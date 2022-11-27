package Entity;

public class Group {
	private long id;
	private long user_id;
	private String name;
	
	public Group(long id, long user_id, String name) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
