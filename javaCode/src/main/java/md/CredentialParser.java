package md;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class CredentialParser {
	private File credentialFile;

	public CredentialParser(File credFile) {
		this.credentialFile = Objects.requireNonNull(credFile);
		try {
			if (!this.credentialFile.exists()) {
				this.credentialFile.getParentFile().mkdirs();
				this.credentialFile.createNewFile();
				FileWriter writer = new FileWriter(this.credentialFile);
				writer.write("username=" + System.lineSeparator());
				writer.write("password=");
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Credentials doMagic() {
		try (BufferedReader br = new BufferedReader(new FileReader(credentialFile))) {
			Credentials c = new Credentials();
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("username="))
					c.username = line.substring(9);
				else if (line.startsWith("password="))
					c.password = line.substring(9);
			}
			if (c.username == null || c.password == null || c.username.isEmpty() || c.password.isEmpty())
				throw new RuntimeException(
						"CredentialFile illformatted. Must have the following format:\nusername=yourname\npassword=yourpass");
			return c;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public class Credentials {
		public String username;
		public String password;
	}
}
