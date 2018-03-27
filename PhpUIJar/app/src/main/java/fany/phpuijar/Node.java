package fany.phpuijar;

import java.util.ArrayList;
import java.util.List;

public interface Node {
	  String getName();

	  void addChild(Node node);

	  ArrayList<Node> getChildren();
	  ArrayList<String> getChildrenName();
	  void setSelectedItemPosition(int position);
	  int getSelectedItemPosition();
	}

	