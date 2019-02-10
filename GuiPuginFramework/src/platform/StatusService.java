package platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import platform.services.IStatusService;

public class StatusService extends Observable implements IStatusService {
	
	private List<String> posts; 
	
	public StatusService() {
		this.posts = new ArrayList<>();
	}

	@Override
	public void postStatus(String status) {
		this.posts.add(status);
		setChanged();
		notifyObservers(status);
		clearChanged();
	}
	
	public List<String> getPosts() {
		return this.posts;
	}

}
