package edu.rosehulman.p2p.app;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.rosehulman.p2p.listeners.IDownloadListener;
import edu.rosehulman.p2p.listeners.IRequestLineListener;
import edu.rosehulman.p2p.listeners.IRequestLogListener;
import edu.rosehulman.p2p.listeners.ISendLogListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class StatusPanel extends JPanel implements IRequestLogListener, IRequestLineListener, IDownloadListener {

	private static final long serialVersionUID = -708353093542621959L;
	private JScrollPane statusScrollPane;
	private JTextArea statusTextArea;
	private JScrollPane requestLogScrollPane;
	private DefaultListModel<String> requestLogListModel;
	private JList<String> requestLogList;

	public StatusPanel() {
		super(new BorderLayout());
		configure();
		this.setPreferredSize(this.getPreferredSize());
	}
	
	private void configure() {
		this.setBorder(BorderFactory.createTitledBorder("Activity"));

		JPanel panel = new JPanel(new BorderLayout());
		this.statusTextArea = new JTextArea("");
		this.statusTextArea.setRows(10);
		this.statusTextArea.setSize(this.statusTextArea.getPreferredSize());
		this.statusScrollPane = new JScrollPane(this.statusTextArea);
		this.statusScrollPane.setSize(this.statusScrollPane.getPreferredSize());
		panel.add(new JLabel("Activity Log", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(this.statusScrollPane, BorderLayout.CENTER);
		this.add(panel, BorderLayout.CENTER);
		panel.setSize(panel.getPreferredSize());
		

		panel = new JPanel(new BorderLayout());
		this.requestLogListModel = new DefaultListModel<>();
		this.requestLogList = new JList<>(this.requestLogListModel);
		this.requestLogScrollPane = new JScrollPane(this.requestLogList);
		
		panel.add(new JLabel("Request Log", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(this.requestLogScrollPane, BorderLayout.CENTER);
		panel.setSize(panel.getPreferredSize());
		this.add(panel, BorderLayout.EAST);
	}

	
	@Override
	public void requestLineUpdated(Collection<IPacket> packets) {
		this.requestLogListModel.clear();
		int i = 0;
		for (IPacket p : packets) {
			this.requestLogListModel.addElement(++i + " : " + p.getCommand() + " => " + p.getObject());
		}
	}

	@Override
	public void downloadComplete(IHost host, String file) {
		this.postStatus("Download of " + file + " from " + host + " complete!");
	}

	public void postStatus(String msg) {
		this.statusTextArea.append(msg + IProtocol.LF);
		this.statusTextArea.setCaretPosition(this.statusTextArea.getDocument().getLength());
	}

	@Override
	public void newRequestUpdate(IPacket p, IHost remoteHost) {
		this.postStatus(p.getCommand() + " recieved from " + remoteHost.toString());
	}

}
