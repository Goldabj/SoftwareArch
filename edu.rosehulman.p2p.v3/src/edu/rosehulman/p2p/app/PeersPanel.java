package edu.rosehulman.p2p.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListSelectionModel;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.packets.AttachPacket;
import edu.rosehulman.p2p.impl.packets.DetachPacket;
import edu.rosehulman.p2p.impl.packets.GetPacket;
import edu.rosehulman.p2p.impl.packets.ListPacket;
import edu.rosehulman.p2p.listeners.IConnectionListener;
import edu.rosehulman.p2p.listeners.IListingListener;
import edu.rosehulman.p2p.listeners.IRequestLogListener;
import edu.rosehulman.p2p.listeners.ISendLogListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class PeersPanel extends JPanel implements IRequestLogListener, ISendLogListener, IListingListener, IConnectionListener {
	private static final long serialVersionUID = 1L;

	private StatusPanel statusPanel;
	private IP2PMediator mediator;

	private JPanel newConnectionPanel;

	private JTextField hostNameField;
	private JTextField portField;

	private JButton connectButton;
	private JButton disconnectButton;
	private JButton listFileButton;

	private JScrollPane peerListScrollPane;
	private JList<IHost> peerList;
	private DefaultListModel<IHost> peerListModel;

	private JScrollPane fileListingPane;
	private JList<String> fileList;
	private DefaultListModel<String> fileListModel;
	private JButton downloadDirect;

	public PeersPanel(StatusPanel statusPanel, IP2PMediator mediator) {
		super(new BorderLayout());
		this.mediator = mediator;
		this.statusPanel = statusPanel;
		configure();
	}

	private void configure() {
		this.setBorder(BorderFactory.createTitledBorder("Remote Connections"));
		this.newConnectionPanel = new JPanel();

		this.hostNameField = new JTextField("");
		this.hostNameField.setColumns(25);

		this.portField = new JTextField("");
		this.portField.setColumns(8);

		this.connectButton = new JButton("Connect");
		this.newConnectionPanel.add(new JLabel("Host: "));
		this.newConnectionPanel.add(this.hostNameField);
		this.newConnectionPanel.add(new JLabel("Port: "));
		this.newConnectionPanel.add(this.portField);
		this.newConnectionPanel.add(this.connectButton);
		//this.newConnectionPanel.setSize(this.newConnectionPanel.getPreferredSize());

		this.connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String host = hostNameField.getText();
					int port = Integer.parseInt(portField.getText());
					final IHost remoteHost = new Host(host, port);

					Thread runner = new Thread() {
						public void run() {
							statusPanel.postStatus("Trying to connect to " + remoteHost + " ...");

							IPacket attachPacket = new AttachPacket(IProtocol.PROTOCOL, remoteHost.toString(),
									mediator.getLocalHost().getHostAddress(), mediator.getLocalHost().getPort() + "",
									mediator.newSequenceNumber());
							try {
								mediator.performAction(IProtocol.ATTACH, attachPacket, remoteHost, new HashMap<>());
							} catch (Exception exp) {
								statusPanel.postStatus("An error occured while connecting: " + exp.getMessage());
							}
						}
					};
					runner.start();
				} catch (Exception ex) {
					statusPanel.postStatus("Connection could not be established: " + ex.getMessage());
				}
			}
		});

		this.add(this.newConnectionPanel, BorderLayout.NORTH);

		JPanel peerListPanel = new JPanel(new BorderLayout());
		peerListPanel.add(new JLabel("List of Peers", JLabel.CENTER), BorderLayout.NORTH);

		this.peerListModel = new DefaultListModel<>();
		this.peerList = new JList<>(this.peerListModel);
		this.peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.peerListScrollPane = new JScrollPane(this.peerList);
		this.listFileButton = new JButton("List Files");
		this.disconnectButton = new JButton("Disconnect");
		peerListPanel.add(this.peerListScrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.add(disconnectButton);
		buttonPanel.add(listFileButton);
		//buttonPanel.setSize(buttonPanel.getPreferredSize());
		peerListPanel.add(buttonPanel, BorderLayout.SOUTH);
		//peerListPanel.setSize(peerListPanel.getPreferredSize());

		this.disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IHost remoteHost = peerList.getSelectedValue();
				if (remoteHost == null) {
					PeersPanel.this.showDialog("You must first select a peer from the list above!", "P2P Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					IPacket detachPacket = new DetachPacket(IProtocol.PROTOCOL, remoteHost.toString(),
							mediator.getLocalHost().getHostAddress(), mediator.getLocalHost().getPort() + "");
					mediator.performAction(IProtocol.DETACH, detachPacket, remoteHost, new HashMap<>());
					statusPanel.postStatus("Disconnected from " + remoteHost + "!");
				} catch (Exception ex) {
					statusPanel.postStatus("Error disconnecting to " + remoteHost + "!");
				}
			}
		});

		this.listFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final IHost remoteHost = peerList.getSelectedValue();
				if (remoteHost == null) {
					PeersPanel.this.showDialog("You must first select a peer from the list above!", "P2P Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				Thread thread = new Thread() {
					public void run() {
						try {
							IPacket listPacket = new ListPacket(IProtocol.PROTOCOL, remoteHost.toString(),
									mediator.getLocalHost().getHostAddress(), mediator.getLocalHost().getPort() + "",
									mediator.newSequenceNumber());
							mediator.performAction(IProtocol.LIST, listPacket, remoteHost, new HashMap<>());
							statusPanel.postStatus("File listing request sent to " + remoteHost + "!");
						} catch (Exception e) {
							statusPanel.postStatus("Error sending list request to " + remoteHost + "!");
						}
					}
				};
				thread.start();
			}
		});

		JPanel fileListPanel = new JPanel(new BorderLayout());
		fileListPanel.add(new JLabel("List of files in the selected peer", JLabel.CENTER), BorderLayout.NORTH);
		this.fileListModel = new DefaultListModel<>();
		this.fileList = new JList<>(this.fileListModel);
		this.fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fileListingPane = new JScrollPane(this.fileList);
		this.downloadDirect = new JButton("Download the selected file");
		fileListPanel.add(this.fileListingPane, BorderLayout.CENTER);
		fileListPanel.add(this.downloadDirect, BorderLayout.SOUTH);
		//fileListPanel.setSize(fileListPanel.getPreferredSize());

		this.downloadDirect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final IHost remoteHost = peerList.getSelectedValue();
				final String fileName = fileList.getSelectedValue();
				if (remoteHost == null || fileName == null) {
					PeersPanel.this.showDialog("You must have a peer and a file selected from the lists above!", "P2PError",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				Thread thread = new Thread() {
					public void run() {
						try {
							IPacket getPacket = new GetPacket(IProtocol.PROTOCOL, remoteHost.toString(),
									mediator.getLocalHost().getHostAddress(), mediator.getLocalHost().getPort() + "",
									mediator.newSequenceNumber(), fileName);
							mediator.performAction(IProtocol.GET, getPacket, remoteHost, new HashMap<>());
							statusPanel.postStatus("Getting file " + fileName + " from " + remoteHost + "...");
						} catch (Exception e) {
							statusPanel.postStatus("Error sending the get file request to " + remoteHost + "!");
						}
					}
				};
				thread.start();
			}
		});

		this.add(peerListPanel, BorderLayout.WEST);
		this.add(fileListPanel, BorderLayout.CENTER);
	}

	@Override
	public void newSendUpdate(IPacket p, IHost remoteHost) {
		switch (p.getCommand()) {
		case IProtocol.ATTACH_OK:
			this.connectionRequestUpdate(remoteHost, true);
			this.connectionEstablished(remoteHost);
			break;
		case IProtocol.ATTACH_NOK:
			this.connectionRequestUpdate(remoteHost, false);
			break;
		case IProtocol.DETACH:
			this.connectionTerminated(remoteHost);
			break;
		}

	}

	@Override
	public void newRequestUpdate(IPacket p, IHost remoteHost) {
		switch (p.getCommand()) {
		case IProtocol.ATTACH_OK:
			this.connectionRequestUpdate(remoteHost, true);
			this.connectionEstablished(remoteHost);
			break;
		case IProtocol.ATTACH_NOK:
			this.connectionRequestUpdate(remoteHost, false);
			break;
		case IProtocol.DETACH:
			this.connectionTerminated(remoteHost);
			break;
		}
	}

	private void connectionRequestUpdate(IHost remoteHost, boolean connected) {
		if (connected) {
			statusPanel.postStatus("Connected to " + remoteHost);
		} else {
			statusPanel.postStatus("Could not connect to " + remoteHost + ". Please try again!");
		}

	}

	public void connectionTerminated(IHost host) {
		this.peerListModel.removeElement(host);
	}

	public void connectionEstablished(IHost host) {
		this.peerListModel.addElement(host);
	}

	@Override
	public void listingReceived(IHost host, List<String> listing) {
		statusPanel.postStatus("File listing received from " + host + "!");
		this.fileListModel.clear();
		for (String f : listing) {
			this.fileListModel.addElement(f);
		}
	}
	
	public void showDialog(String message, String title, int type) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void newConnection(IHost remoteHost) {
		this.connectionRequestUpdate(remoteHost, true);
		this.connectionEstablished(remoteHost);
		
	}

	@Override
	public void connectionRefused(IHost remoteHost) {
		this.statusPanel.postStatus("connection refused from " + remoteHost.toString());
		
	}

}
