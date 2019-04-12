# Create Report as XML Extension

## 1. Start SQL Developer

Start SQL Developer.

## 2. Create Report

Select `View`->`Reports` in the main menu.

![Reports](./images/main_menu_view_reports.png)

Right click on `User Defined Reports` and select `New Report...`.

![New Report...](./images/reports_new_report.png)

and you get this `Create Report` dialog:

![Create Report](./images/create_report.png)

Enter `All objects` in the name field and 

```sql
select * from all_objects
order by owner, object_type, object_name
```

in the SQL field and press the `Apply` button.

`User Defined Reports` is shown in italic letters. 

![User Defined Reports (unsaved)](./images/user_defined_reports_italic.png)

This means that the report is not yet saved. Press the `Save All` button in the toolbar to save the report.

![Save All](./images/toolbar_save_all.png)

Click on the `All Objects` report and select a connection from the `Select Connection` dialog to show the report.

![Select Connection](./images/select_connection.png)

## 3. Save Report as XML File

Right click on the `All Objects` report and select `Save As...`.

![Save As...](./images/report_save_as.png)

And save the report as `all_objects.xml` in a folder of your choice.

## 4. Delete Report

Right click on the `All Objects` report and select `Delete` and confirm the deletion.

![Save As...](./images/report_delete.png)

## 5. Configure Report as XML Extension

Select `Tools`->`Preferences` from the main menu.

![Preferences](./images/main_menu_tools_preferences.png)

In the preferences dialog click on `User Defined Extensions` under the `Database` node.

![Preferences](./images/preferences.png)

Press the `Add Row` button and select `REPORT` in the type field and browse for the previously saved `all_objects.xml`.

![Preferences](./images/preferences2.png)

And press the `OK`.

## 6. Restart SQL Developer

You must restart SQL developer for the changes to take effect. Close SQL Developer and start it again.

## 7. Run Report

Now you find the `All Objects` report in the Report window under the `Shared Report` node.

![Shared Reports](./images/shared_reports.png)

Click on `All Objects` to run it.
