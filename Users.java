package vo;

public class Users {
    private String username;

    private String chrname;

    private String password;

    private String role;

    
    public Users() {
	}

	public Users(String username, String chrname, String password, String role) {
		this.username = username;
		this.chrname = chrname;
		this.password = password;
		this.role = role;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getChrname() {
        return chrname;
    }

    public void setChrname(String chrname) {
        this.chrname = chrname == null ? null : chrname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role == null ? null : role.trim();
    }
}