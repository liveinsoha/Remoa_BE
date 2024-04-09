package Remoa.BE.utill;

public final class CommonFunction {


    public CommonFunction() {
        throw new AssertionError();
    }


    public static Long getCategoryId(String name){
        Long catagoryId = (long) 0; // 0 은 all 로 인식

        if("idea".equals(name)){catagoryId = (long)1;}
        if("marketing".equals(name)){catagoryId = (long)2;}
        if("design".equals(name)){catagoryId = (long)3;}
        if("video".equals(name)){catagoryId = (long)4;}
        if("digital".equals(name)){catagoryId = (long)5;}
        if("etc".equals(name)){catagoryId = (long)6;}

        return catagoryId;
    }

}
