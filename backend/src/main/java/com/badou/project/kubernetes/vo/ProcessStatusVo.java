package com.badou.project.kubernetes.vo;

public class ProcessStatusVo {
    //进程所属用户ID
    private String uid;
    //进程ID
    private int pid;
    //父进程ID
    private int ppid;
    //进程的CPU占用率
    private int c;
    //进程启动时间
    private String stime;
    //进程所属的终端
    private String tty;
    //进程使用的CPU时间
    private String time;
    //启动进程的命令
    private String cmd;

    public ProcessStatusVo(String uid, int pid, int ppid, int c, String stime, String tty, String time, String cmd) {
        this.uid = uid;
        this.pid = pid;
        this.ppid = ppid;
        this.c = c;
        this.stime = stime;
        this.tty = tty;
        this.time = time;
        this.cmd = cmd;
    }

    /**
     * 获取进程所属用户 ID
     * @return 进程所属用户 ID
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置进程所属用户 ID
     * @param uid 进程所属用户 ID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取进程 ID
     * @return 进程 ID
     */
    public int getPid() {
        return pid;
    }

    /**
     * 设置进程 ID
     * @param pid 进程 ID
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * 获取父进程 ID
     * @return 父进程 ID
     */
    public int getPpid() {
        return ppid;
    }

    /**
     * 设置父进程 ID
     * @param ppid 父进程 ID
     */
    public void setPpid(int ppid) {
        this.ppid = ppid;
    }

    /**
     * 获取进程的 CPU 占用率
     * @return 进程的 CPU 占用率
     */
    public int getC() {
        return c;
    }

    /**
     * 设置进程的 CPU 占用率
     * @param c 进程的 CPU 占用率
     */
    public void setC(int c) {
        this.c = c;
    }

    /**
     * 获取进程启动时间
     * @return 进程启动时间
     */
    public String getStime() {
        return stime;
    }

    /**
     * 设置进程启动时间
     * @param stime 进程启动时间
     */
    public void setStime(String stime) {
        this.stime = stime;
    }

    /**
     * 获取进程所属的终端
     * @return 进程所属的终端
     */
    public String getTty() {
        return tty;
    }

    /**
     * 设置进程所属的终端
     * @param tty 进程所属的终端
     */
    public void setTty(String tty) {
        this.tty = tty;
    }

    /**
     * 获取进程使用的 CPU 时间
     * @return 进程使用的 CPU 时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置进程使用的 CPU 时间
     * @param time 进程使用的 CPU 时间
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取启动进程的命令
     * @return 启动进程的命令
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * 设置启动进程的命令
     * @param cmd 启动进程的命令
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "uid='" + uid + '\'' +
                ", pid=" + pid +
                ", ppid=" + ppid +
                ", c=" + c +
                ", stime='" + stime + '\'' +
                ", tty='" + tty + '\'' +
                ", time='" + time + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
