package dataBaseConnection;

public class Item {
	
	private int Id;
	private String message;
	
	public Item(int id, String message) {
		
		this.Id = id;
		this.message = message;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Item [Id=" + Id + ", message=" + message + "]";
	}
	
	

}
