package org.sobadfish.title.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import org.sobadfish.title.TitleMain;
import org.sobadfish.title.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sobadfish
 * @date 2022/9/15
 */
public class PlayerData {

    public String name;


    public TitleData wearTitle;

    public List<TitleData> titles = new ArrayList<>();


    public PlayerData(String name){
        this.name = name;
    }




    public void wearTitle(TitleData title){
        if(title != null){
            if(titles.contains(title)){
                this.wearTitle = title;
            }
        }else{
            this.wearTitle = null;
        }

        if(TitleMain.playerManager != null) {
            TitleMain.playerManager.wearTitle(name, title);
        }else{
            TitleMain.sendMessageToConsole("&c未连接到数据库,可能会出现称号丢失问题。请检查数据库连接参数是否正常");
        }
    }

    public void addTitle(TitleData title){
        TitleData titleData = title;
        if(titles.contains(title)){
            titleData = titles.get(titles.indexOf(title));
            if(title.outTime == null){
                titleData.outTime = null;
            }else{
                int time = Tools.calLastedTime(titleData.outTime);
                if(time < 0){
                    titleData.outTime = null;
                }else{
                    titleData.outTime = Tools.mathTime(time);
                }

            }

        }else{
            titles.add(titleData);
        }
        this.wearTitle = titleData;
        if(TitleMain.playerManager != null) {
            TitleMain.playerManager.addTitle(name, titleData);
        }else{
            TitleMain.sendMessageToConsole("&c未连接到数据库,可能会出现称号丢失问题。请检查数据库连接参数是否正常");
        }
    }



    public boolean removeTitle(String name){
        for(TitleData titleData:new ArrayList<>(titles)){
            if(titleData.name.equalsIgnoreCase(name)){
                if(wearTitle != null){
                    if(wearTitle.equals(titleData)){
                        wearTitle = null;
                    }
                }
                if(TitleMain.playerManager != null) {
                    TitleMain.playerManager.removeTitle(this.name, name);
                }else{
                    TitleMain.sendMessageToConsole("&c未连接到数据库,可能会出现称号丢失问题。请检查数据库连接参数是否正常");
                }
                titles.remove(titleData);
                return true;
            }
        }

        return false;
    }

    public Player getPlayer(){
        return Server.getInstance().getPlayer(name);
    }

    /**
     * 移除过期的称号
     * */
    public void deleteTimeOut(){
        for(TitleData titleData:new ArrayList<>(titles)){
            if(titleData.outTime == null || "".equalsIgnoreCase(titleData.outTime)){
                continue;
            }
            if(Tools.isOut(titleData.outTime)){
                if(wearTitle != null && wearTitle.equals(titleData)){
                    wearTitle = null;
                }

                if(TitleMain.playerManager != null){
                    TitleMain.playerManager.removeTitle(this.name, titleData.name);
                }
                titles.remove(titleData);

            }
        }
    }

    public static class TitleData{

        public int id;
        /**
         * 称号名称
         * */
        public String name;

        /**
         * 称号到期时间
         * */
        public String outTime = "";

        /**
         * 称号执行指令
         * */
        public String cmd;

        /**
         * 称号执行指令延迟
         * */
        public int delay;

        public TitleData(String name,String outTime,String cmd,int delay){
            this.name = name;
            this.outTime = outTime;
            this.cmd = cmd;
            this.delay = delay;
        }

        public TitleData(String name,int sec,String cmd,int delay){
            this.name = name;
            if(sec <= 0){
                this.outTime = null;
            }else{
                this.outTime = Tools.mathTime(sec);
            }

            this.cmd = cmd;
            this.delay = delay;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof TitleData){
                TitleData titleData = (TitleData) o;
                return name.equals(titleData.name);
            }
            return false;

        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof PlayerData){
           PlayerData that = (PlayerData) o;
           return name.equals(that.name);
       }
       return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
