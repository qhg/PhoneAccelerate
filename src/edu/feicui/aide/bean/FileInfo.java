package edu.feicui.aide.bean;

public class FileInfo {
	/**
	 * 文件类型
	 */
	public String fileType;
	/**
	 * 文件大小
	 */
	public long fileLen;

	/**
	 * false表示正在查找数据<br/>
	 * true 表示查找完成
	 */
	public boolean isFinish;

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getFileLen() {
		return fileLen;
	}

	public void setFileLen(long fileLen) {
		this.fileLen = fileLen;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public FileInfo() {

	}

	public FileInfo(String fileType, long fileLen, boolean isFinish) {
		super();
		this.fileType = fileType;
		this.fileLen = fileLen;
		this.isFinish = isFinish;
	}

	@Override
	public String toString() {
		return "FileInfo [fileType=" + fileType + ", fileLen=" + fileLen
				+ ", isFinish=" + isFinish + "]";
	}
}
