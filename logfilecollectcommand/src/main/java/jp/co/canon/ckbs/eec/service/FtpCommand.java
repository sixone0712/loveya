package jp.co.canon.ckbs.eec.service;

public class FtpCommand {
    static Command createCommandFromName(String str){
        if (str.equals("list")){
            return new ListFtpCommand();
        }
        if (str.equals("get")){
            return new GetFtpCommand();
        }
        return null;
    }

    public static void main(String[] args){
        if (args.length < 1){
            System.out.println("Command Error");
            return;
        }
        Command command = createCommandFromName(args[0]);
        if (command == null){
            System.out.println("Invalid Command");
            return;
        }

        String[] newArgs = new String[args.length - 1];
        for(int idx = 1; idx < args.length; ++idx){
            newArgs[idx - 1] = args[idx];
        }

        command.execute(newArgs);
    }
}
