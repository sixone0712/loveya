package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import java.util.ArrayList;

public class ContextChecker {
    ContextList ctxList = null;

    private ContextChecker(){

    }

    public static ContextChecker fromString(String context){
        if (context == null){
            return null;
        }
        ContextList ctxList = ContextList.parseContextString(context);
        if (ctxList == null){
            return null;
        }
        Context[] ctxs = ctxList.getContextByName("DE");
        if (ctxs.length > 1){
            return null;
        }
        ContextChecker checker = new ContextChecker();
        checker.ctxList = ctxList;
        return checker;
    }

    public String getDeviceName(){
        Context[] ctxs = ctxList.getContextByName("DE");
        return ctxs[0].get(0);
    }

    public static boolean isValidContextString(String context){
        if (context != null){
            ContextList ctxList = ContextList.parseContextString(context);
            if (ctxList == null){
                return false;
            }            
            Context[] ctxs = ctxList.getContextByName("DE");
            if (ctxs.length > 1){
                return false;
            }
            return true;
        }
        return true;
    }

    public static String getDeviceNameFromContextString(String context){
        if (context != null){
            ContextList ctxList = ContextList.parseContextString(context);
            if (ctxList == null){
                return null;
            }            
            Context[] ctxs = ctxList.getContextByName("DE");
            if (ctxs.length > 1){
                return null;
            }
            if (ctxs.length == 1){
                return ctxs[0].get(0);
            }
            return null;
        }
        return null;
    }


    static class Context {
        private String name;
        private ArrayList<String> list = new ArrayList<String>();
    
        public Context(String name){
            this.name = name;
        }
    
        public String getName(){
            return this.name;
        }
    
        public void add(String value){
            list.add(value);
        }
    
        public String get(int idx){
            if (idx >= list.size()){
                return null;
            }
            return list.get(idx);
        }
    }

    static class ContextList {
        private ArrayList<Context> list = new ArrayList<>();
    
        private void add(Context ctx){
            list.add(ctx);
        }
    
        public Context[] getContextByName(String ctxName){
            ArrayList<Context> returnList = new ArrayList<>();
            for(Context ctx : list){
                if (ctx.getName().equals(ctxName)){
                    returnList.add(ctx);
                }
            }
            return returnList.toArray(new Context[0]);
        }
    
        public static ContextList parseContextString(String contextStr){
            if (contextStr != null){
                ContextList list = new ContextList();
                String[] data = contextStr.split("_");
                int idx = 0;
                
                for(idx = 0; idx < data.length; ++idx){
                    if (data[idx].equals("DE")){
                        if (data.length <= idx + 1){
                            return null;
                        }
                        if (data[idx+1].length() > 8 || data[idx+1].length() == 0){
                            return null;
                        }
                        Context ctx = new Context("DE");
                        ctx.add(data[idx+1]);
                        idx++;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("PR")){
                        if (data.length <= idx + 1){
                            return null;
                        }
                        if (data[idx+1].length() > 8 || data[idx+1].length() == 0){
                            return null;
                        }
                        Context ctx = new Context("PR");
                        ctx.add(data[idx+1]);
                        idx++;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("LO")){
                        if (data.length <= idx + 1){
                            return null;
                        }
                        if (data[idx+1].length() > 32 || data[idx+1].length() == 0){
                            return null;
                        }
                        Context ctx = new Context("LO");
                        ctx.add(data[idx+1]);
                        idx++;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("P")){
                        if (data.length <= idx + 2){
                            return null;
                        }
                        try {
                            Integer.parseInt(data[idx+1]);
                            Integer.parseInt(data[idx+2]);
                        } catch (NumberFormatException e){
                            return null;
                        }
                        Context ctx = new Context("P");
                        ctx.add(data[idx+1]);
                        ctx.add(data[idx+2]);
                        idx += 2;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("S")){
                        if (data.length <= idx + 2){
                            return null;
                        }
                        try {
                            Integer.parseInt(data[idx+1]);
                            Integer.parseInt(data[idx+2]);
                        } catch (NumberFormatException e){
                            return null;
                        }
                        Context ctx = new Context("S");
                        ctx.add(data[idx+1]);
                        ctx.add(data[idx+2]);
                        idx += 2;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("M")){
                        if (data.length <= idx + 2){
                            return null;
                        }
                        try {
                            Integer.parseInt(data[idx+1]);
                            Integer.parseInt(data[idx+2]);
                        } catch (NumberFormatException e){
                            return null;
                        }
                        Context ctx = new Context("M");
                        ctx.add(data[idx+1]);
                        ctx.add(data[idx+2]);
                        idx += 2;
                        list.add(ctx);
                        continue;
                    }
                    if (data[idx].equals("G")){
                        if (data.length <= idx + 1){
                            return null;
                        }
                        if (data[idx+1].length() > 16 || data[idx+1].length() == 0){
                            return null;
                        }
                        Context ctx = new Context("G");
                        ctx.add(data[idx+1]);
                        idx++;
                        list.add(ctx);
                        continue;
                    }
                    return null;
                }
                return list;
            }
            return null;
        }
    }
}