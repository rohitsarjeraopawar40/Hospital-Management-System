package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Hospital {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="Rohit@123";

    public static void main(String[] args) {
        Scanner scanner =new Scanner(System.in);
        //Connection connection= DriverManager.getConnection(url,username,password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        try {
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient= new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("Hospital Management System");
                System.out.println("1.Add Patient");
                System.out.println("2.View Patient");
                System.out.println("3.View Doctor");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter Your Choise");
                int choice=scanner.nextInt();
                switch (choice){
                    case 1:
                        //add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //view doctor
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointments
                        bookAppointment(patient, doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        //exit
                        return;

                    default:
                        System.out.println("Enter Valid Choice From 1 To 5:");
                        break;
                }

            }


        }
        catch (SQLException e){
            e.printStackTrace();
        }


    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection, Scanner scanner ){
        System.out.println("Enter Patient Id:");
        int patientId=scanner.nextInt();
        System.out.println("Enter Doctor Id:");
        int doctorId =scanner.nextInt();
        System.out.println("Enter Appointment Date(yyyy-mm-dd):");
        String appointmentDate= scanner.next();
        if(patient.getPatientbyId(patientId) && doctor.getDoctorById(doctorId)){
            if (checkDoctorAvailable(doctorId,appointmentDate,connection)){
                String appointmentQuery ="insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try {
                    PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowaffected =preparedStatement.executeUpdate();
                    if(rowaffected>0){
                        System.out.println("Appointment Booked....!");
                    }
                    else{
                        System.out.println("Failed To Book Appointment....!");
                    }

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Doctor Not Available:");
            }
        }
        else {
            System.out.println("Either Patient or Doctor Not Exist....!");
        }

    }
    public static boolean checkDoctorAvailable(int doctorId,String appointmentDate,Connection connection){
        String query="select count(*) from appointments where doctor_id=? AND appointment_date=?";
        try {

            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet =preparedStatement.executeQuery();
            if(resultSet.next()){
                int count=resultSet.getInt(1);
                if (count==0){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }
}
