package buaa.gui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SourceModel {
	private String index;
	private String number;
	private String title;
	private String content;
	private String score;
	private boolean active;
	private boolean inActive;

	public SourceModel() {
    // No Code;
	}

	public SourceModel(String index,String number,String title,String content,String 
			score,String wordf,boolean active,boolean inActive) {
		super();
		this.index = index;
		this.number = number;
		this.title = title;
		this.content = content;
		this.score = score;
		this.active = active;
		this.inActive = inActive;
		
	}
	public boolean isSelect() {
		return active;
	}
	public void setSelect(boolean select) {
		this.active = select;
	}
	public boolean isInActive() {
		return inActive;
	}
	public void setInActive(boolean select) {
		this.inActive = select;
	}
	
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
}



