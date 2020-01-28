package entity;

/**
 * @ Date 2020/1/13 15:54
 * @ Created by CYF
 * @ Description TODO
 */
public class Person {

    // 姓名
    private String name;
    // 年龄
    private int age;
    // 邮件
    private String email;
    // 描述
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person(String name, int age, String email, String desc) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\''+
                ", age='" + age + '\'' +
                ", email='" + email + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public static void main(String[] args) {
        // 初始化一个对象
        Person person = new Person("张三",20,"123456@qq.com","我是张三");
        // 复制对象
        Person person1 = person;
        // 改变 person1 的属性值
        person.setName("我不是张三了");
        System.out.println("person对象："+person.toString());
        System.out.println("person1对象："+person1.toString());

    }

}
