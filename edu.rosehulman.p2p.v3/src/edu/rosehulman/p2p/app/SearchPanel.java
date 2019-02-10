package edu.rosehulman.p2p.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.rosehulman.p2p.impl.packets.AttachPacket;
import edu.rosehulman.p2p.impl.packets.DetachPacket;
import edu.rosehulman.p2p.impl.packets.FindPacket;
import edu.rosehulman.p2p.impl.packets.GetPacket;
import edu.rosehulman.p2p.listeners.IDownloadListener;
import edu.rosehulman.p2p.listeners.IFoundListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;


public class SearchPanel extends JPanel implements IFoundListener, IDownloadListener {
	private static final long serialVersionUID = -1132951678101635252L;
	private JTextField searchTermField;
	private JTextField searchdepthField;
	private JButton searchButton;
	private JList<SearchResult> searchResultList;
	private DefaultListModel<SearchResult> searchResultListModel;
	private JScrollPane searchResultScrollPane;
	private JButton downloadAfterSearch;
	private IP2PMediator mediator;
	private StatusPanel statusPanel;
	private List<SearchResult> connectionsMade;
	
	public SearchPanel(IP2PMediator med, StatusPanel statusPanel) {
		super(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder("Network File Searching"));
		configure();
		this.mediator = med;
		this.statusPanel = statusPanel;
		this.connectionsMade = new ArrayList<>();
	}
	
	private void configure() {

		JPanel top = new JPanel();
		top.add(new JLabel("Search Term: "));
		this.searchTermField = new JTextField("");
		this.searchTermField.setColumns(15);
		top.add(this.searchTermField);
		top.add(new JLabel("Search Depth: "));
		this.searchdepthField = new JTextField("");
		this.searchdepthField.setColumns(5);
		this.searchButton = new JButton("Search Network");
		top.add(this.searchdepthField);
		top.add(this.searchButton);
		
		this.searchResultListModel = new DefaultListModel<>();
		this.searchResultList = new JList<>(this.searchResultListModel);
		this.searchResultScrollPane = new JScrollPane(this.searchResultList);
		
		this.downloadAfterSearch = new JButton("Download the selected file");
		
		this.add(top, BorderLayout.NORTH);
		this.add(this.searchResultScrollPane, BorderLayout.CENTER);
		this.add(this.downloadAfterSearch, BorderLayout.SOUTH);
		
		this.searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchResultListModel.clear();
				String fileName = searchTermField.getText();
				int depth = 0;
				try {
					depth = Integer.parseInt(searchdepthField.getText());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SearchPanel.this, "dpeth must be a number");
					return;
				}
				// send out a find, and log request
				IHost localHost = mediator.getLocalHost();
				List<IHost> peers = mediator.getPeers();
				int seqNum = mediator.newSequenceNumber();
				List<IHost> visited = new ArrayList<>();
				visited.add(localHost);
				List<IHost> found = new ArrayList<>();
				
				for (IHost rHost : peers) {
					IPacket findPacket = new FindPacket(IProtocol.PROTOCOL, rHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", 
							fileName, seqNum, depth, visited, 1, found);
					mediator.performAction(IProtocol.FIND, findPacket, rHost, new HashMap<>());
				}
				statusPanel.postStatus("Searching for " + fileName + " .....");
				
			}
		
		});
		
		this.downloadAfterSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make connection if needed
				// Get file
				// once download complete, terminate connection if wasn't there already
				SearchResult searchResult = searchResultList.getSelectedValue();
				IHost remoteHost = searchResult.rHost;
				String fileName = searchResult.fileName;
				
				if (!mediator.isConnectedToPeer(remoteHost)) {
					// make connection and add to list
					connectionsMade.add(new SearchResult(fileName, remoteHost));
					IPacket attachPacket = new AttachPacket(IProtocol.PROTOCOL, remoteHost.toString(), mediator.getLocalHost().getHostAddress(),
							mediator.getLocalHost().getPort() + "", mediator.newSequenceNumber());
					mediator.performAction(IProtocol.ATTACH, attachPacket, remoteHost, new HashMap<>());
				} 
				// download directly
				IPacket getPacket = new GetPacket(IProtocol.PROTOCOL, remoteHost.toString(), mediator.getLocalHost().getHostAddress(), 
						mediator.getLocalHost().getPort() + "", mediator.newSequenceNumber(), fileName);
				mediator.performAction(IProtocol.GET, getPacket, remoteHost, new HashMap<>());
			}
			
		});
		
	}

	@Override
	public void fileFoundUpdate(String fileName, List<IHost> foundHost) {
		for (IHost res : foundHost) {
			SearchResult result = new SearchResult(fileName, res);
			this.searchResultListModel.addElement(result);
		}
	}
	
	public class SearchResult {
		String fileName;
		IHost rHost;
		
		public SearchResult(String fileName, IHost rHost) {
			this.fileName = fileName;
			this.rHost = rHost;
		}
		
		@Override
		public String toString() {
			return fileName + "  -  " + rHost.toString();
		}
	}

	@Override
	public void downloadComplete(IHost remoteHost, String fileName) {
		for (SearchResult r : this.connectionsMade) {
			if (r.fileName.equals(fileName) && r.rHost.equals(remoteHost)) {
				disconnectFromHost(remoteHost);
				break;
			}
		}
		
	}
	
	private void disconnectFromHost(IHost remoteHost) {
		for (int i = 0; i < this.connectionsMade.size(); i++) {
			SearchResult r = this.connectionsMade.get(i);
			if (r.rHost.equals(remoteHost)) {
				this.connectionsMade.remove(i);
				break;
			}
		}
		IPacket detachPacket = new DetachPacket(IProtocol.PROTOCOL, remoteHost.toString(), mediator.getLocalHost().getHostAddress(),
				mediator.getLocalHost().getPort() + "");
		mediator.performAction(IProtocol.DETACH, detachPacket, remoteHost, new HashMap<>());
	}

}
