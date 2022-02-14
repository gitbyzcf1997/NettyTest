package com.zcf.nettystudy.netty.v3;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-14-15:56
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class TankMsg {
    public int x,y;

    public TankMsg(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMsg:"+x+","+y;
    }
}
