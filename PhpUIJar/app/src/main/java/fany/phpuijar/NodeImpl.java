package fany.phpuijar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NodeImpl implements Node {
	  private final String name;
	  private final ArrayList<String> nodeName;
	  private final ArrayList<Node> nodes;
	  private int position;
	  public NodeImpl(String name) {
	    this.name = name;
	    this.nodes = new ArrayList<Node>();
	    this.nodeName = new ArrayList<String>();
	  }

	  public String getName() {
	    return name;
	  }

	  public void addChild(Node node) {
	    nodes.add(node);
	    nodeName.add(node.getName());
	  }

	  public ArrayList<String> getChildrenName() {
	    return nodeName;
	  }
	  public ArrayList<Node> getChildren() {
		    return nodes;
		  }

	@Override
	public void setSelectedItemPosition(int position) {
		// TODO Auto-generated method stub
		this.position=position;
	}

	@Override
	public int getSelectedItemPosition() {
		// TODO Auto-generated method stub
		return position;
	}
}