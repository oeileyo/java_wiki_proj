public class StringWriter {

    public void printString (String s){
        s = s.replace("\\", "");

        Integer first_i = 0;
        Integer i = 65;
        while (i < s.length()){
            if (s.charAt(i) == ' '){
                System.out.println(s.substring(first_i, i));
                first_i = i+1;
                i += 65;

            } else {
                i += 1;
            }
        }
        System.out.println(s.substring(first_i, s.length()));
    }
}
