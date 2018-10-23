package mainapp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tableobjects.Project;
import tableobjects.Task;
import tableobjects.User;
import connection.SynkConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import userinterface.SynkApp;

public class MainAppUI {
    ObservableList<Project> projItems = FXCollections.observableArrayList();
    ObservableList<Task> taskItems = FXCollections.observableArrayList();
    ObservableList<User> userItems = FXCollections.observableArrayList();

    @FXML private ListView<Project> listViewProjects;
    @FXML private ListView<Task> listViewTasks;
    @FXML private ListView<User> listViewUsers;

    //TODO move some functionality to a separate class, maybe a UI controller of some sorts
    //TODO add functionality to only display projects that the user is on, maybe through saved session user_id


    @FXML
    public void displayForm() {
        SynkApp.getInstance().showForm("/fxml/makeprojectform.fxml");
    }
    public void initialize(){
        listViewProjects.setItems(AppData.getInstance().getProjItems());
    }
    @FXML
    public void narrowUsers(){
        if(listViewTasks.getSelectionModel().getSelectedIndex() ==-1){
            return;
        }
        int taskID = listViewTasks.getItems().get(listViewTasks.getSelectionModel().getSelectedIndex()).getTaskID();

        userItems.clear();
        try {
            ResultSet rs;
            PreparedStatement stmt = SynkConnection.con.prepareStatement(
                    "SELECT u.USER_ID,u.USERNAME\n" +
                            " FROM users u,user_task_assigned uta\n" +
                            " WHERE u.user_id = uta.user_id AND uta.task_id = " + taskID);
            rs = stmt.executeQuery();
            while(rs.next()){
                userItems.add(new User(rs.getInt("user_id"), rs.getString("username")));
            }
        } catch (SQLException e){
            System.err.println(e);
        }
        listViewUsers.setItems(userItems);
    }
    @FXML
    public void showProjectTasksAndUsers(){
        if(listViewProjects.getSelectionModel().getSelectedIndex() == -1){
            return;
        }
        taskItems.clear();
        userItems.clear();

        ResultSet rs;
        int projId = listViewProjects.getItems().get(listViewProjects.getSelectionModel().getSelectedIndex()).getProjId();
        try {
            PreparedStatement stmt = SynkConnection.con.prepareStatement("SELECT * FROM tasks WHERE proj_id =" + projId);
            rs = stmt.executeQuery();
            while(rs.next()){
                taskItems.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("task_desc"),
                        rs.getString("task_name"),
                        rs.getInt("proj_id")));
            }
        } catch (SQLException e){
            System.err.println(e);
        }
        try {
            PreparedStatement stmt = SynkConnection.con.prepareStatement(
                    "SELECT * FROM users u,user_proj_assigned upa " +
                            "WHERE u.user_id = upa.user_id AND upa.proj_id = " +  projId);
            rs = stmt.executeQuery();
            while(rs.next()){
                userItems.add(new User(rs.getInt("user_id"), rs.getString("username")));
            }
        } catch (SQLException e){
            System.err.println(e);
        }
        listViewTasks.setItems(taskItems);
        listViewUsers.setItems(userItems);
    }

}
