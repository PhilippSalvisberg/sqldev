# Trigger Hooks

This document is a manual transformation of the deleted webpage https://java.net/projects/jdev-extensions/pages/Trigger-hooks created by John Brock.

**Table of Contents** 

* [1. Actions](#1-actions)
* [2. About `<controller-class>` and `<command-class>` tags](#2-about--and--tags)
* [3. Controllers](#3-controllers)
  * [3.1 Trigger Actions, Rules and Controllers Behavior](#31-trigger-actions-rules-and-controllers-behavior)
  * [3.2 When to use an oracle.ide.controller.TriggerController](#32-when-to-use-an-oracleidecontrollertriggercontroller)
* [4. Menus](#4-menus)
  * [4.1 FCP-supplied menu ids are:](#41-fcp-supplied-menu-ids-are)
  * [4.2 FCP-supplied sections are:](#42-fcp-supplied-sections-are)
  * [4.3 JDeveloper menu IDs](#43-jdeveloper-menu-ids)
  * [4.4 Assigning weight and section values to menu and submenu items](#44-assigning-weight-and-section-values-to-menu-and-submenu-items)
* [5. Context Menus](#5-context-menus)
  * [5.1 Extension Listener](#51-extension-listener)
* [6. Editor Menu](#6-editor-menu)
  * [6.1 Use as a standard (non-trigger) hook](#61-use-as-a-standard-non-trigger-hook)
* [7. Accelerators/Shortcut Keys](#7-acceleratorsshortcut-keys)
* [8. Gallery Items](#8-gallery-items)
* [9. Technology Scopes](#9-technology-scopes)
* [10. Editors](#10-editors)
* [11. NodeFactory Recognizers](#11-nodefactory-recognizers)
* [12. IDE Preferences/Settings](#12-ide-preferencessettings)
* [13. Application Preferences/Settings](#13-application-preferencessettings)
* [14. Project Preferences/Settings](#14-project-preferencessettings)
* [15. Content Set Providers](#15-content-set-providers)
* [16. Singleton Registration](#16-singleton-registration)
* [17. Application and Project Migrators](#17-application-and-project-migrators)
* [18. URLFileSystem Hook](#18-urlfilesystem-hook)
* [19. ImportExport Hook](#19-importexport-hook)
* [20. Product hook](#20-product-hook)
* [21. Menu Customizations hook](#21-menu-customizations-hook)
* [22. Bridge Extensions](#22-bridge-extensions)
* [23. On Project Open hook](#23-on-project-open-hook)
* [24. Help Callbacks Hook](#24-help-callbacks-hook)
* [25. Help Hook](#25-help-hook)
* [26. Dockable Hook](#26-dockable-hook)
  * [26.1 Standard Dockable Factory](#261-standard-dockable-factory)
  * [26.2 Drawer Dockable](#262-drawer-dockable)
* [27. Historian Hook](#27-historian-hook)
* [28. External Tools Hook](#28-external-tools-hook)

## 1. Actions

**Name:** actions 

**Description:** Register actions, controllers and commands. 

**Usage Example:**

```xml
<extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
  <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
    <triggers>
      <actions xmlns="http://xmlns.oracle.com/jdeveloper/1013/extension">
        <action id="oracle.jdevimpl.bugfiler.FileABugAction">
          <properties>
            <property name="Name">${FILE_A_BUG_ACTION_NAME}</property>
            <property name="MnemonicKey" rskey="FILE_A_BUG_ACTION_NAME_MNEMONIC" />
            <property name="SmallIcon">${OracleIcons.PLACEHOLDER}</property>
            <property name="LongDescription">${FILE_A_BUG_DESCRIPTION}</property>
            <property name="Category" rskey="BUILD_CATEGORY" />
          </properties>
        </action>
      </actions>
      <controllers xmlns="http://xmlns.oracle.com/ide/extension">
       <controller class="oracle.jdevimpl.bugfiler.ReportABugController">
         <update-rules>
           <update-rule type="always-enabled" action="oracle.jdevimpl.bugfiler.FileABugAction"/>
         </update-rules>
       </controller>
      </controllers>
      <menu-hook  xmlns="http://jcp.org/jsr/198/extension-manifest">
        <toolbar id="javax.ide.view.MAIN_WINDOW_TOOLBAR_ID">
          <section id="oracle.jdeveloper.filebugsection"
                   after="oracle.jdeveloper.TB_RUN_DEBUG_SECTION">
            <item action-ref="oracle.jdevimpl.bugfiler.FileABugAction"/>
          </section>
        </toolbar>
      </menu-hook>
    </triggers>
  </trigger-hooks>
   ...
 </extension>
```

**Availability:** Available. Use of `<controllers>` inline with actions is not supported. 

**XSD:** actions-hook.xsd

**Use Case:** Locating actions at runtime.

## 2. About <controller-class> and <command-class> tags

Use of the `<controller-class>` tag is restricted in the trigger-hooks hook, but is allowed in the standard hooks hook. The `<command-class>` tag can be used in either hook because no client code is necessary to handle updating the command; the extension is initialized on command invocation.

## 3. Controllers

Rule-based controllers are registered outside of actions, allowing for controller reuse in several actions. You should not define the controller-class element in the action if you are using the controllers hook as this will add a redundant controller. Controllers defined inside a `<triggers>` section may be applied to any action (both trigger and normal). Controllers defined inside a `<hooks>` section can only be applied to actions also defined inside a `<hooks>` section, i.e, not trigger actions. Here's what a typical `<controllers>` section looks like:

```xml
<controllers xmlns="http://xmlns.oracle.com/ide/extension">
   <controller class="com.random.FooController">
     <update-rules>
       <!-- note that this rule must be defined in the rules hook for this rule to work -->
       <update-rule rule="on-text-node-single-selection">
         <action id="some.action.id1">
           <label>Perform Action on {0}</label>
           <label-param macroKey="node.name" />
         </action>
         <action id="some.action.id2" />
         <action id="some.action.id3">
           <label>Perform {0} Action</label>
           <label-param>My Action</label-param>
         </action>
       </update-rule>
     </update-rules>
   </controller>
   <controller class="com.random.BarController">
     <update-rules>
       <update-rule rule="context-has-project">
         <action id="some.action.id3"/>
         <action id="some.action.id4"/>
         <action id="some.action.id5"/>
       </update-rule>
     </update-rules>
   </controller>
 </controllers>
```

Here we have a controller com.random.FooController that can be applied to two actions [some.action.id1, some.action.id2], and controller com.random.BarController is applied to 3 actions [some.action.i3, some.action.id4,some.action.id5]. The value of the rule attribute identifies what rule to evaluate to determine whether or not the actions should be enabled. In the above example, the rule with id "on-text-node-single-selection" is used to enable the first two actions, and and "context-has-project" is used to enable the latter 3 actions.

This syntax also supports updating an action's label. As shown for action `some.action.id1` above, you use the optional child elements `<label>` and `<label-param>`. The `<label>` element can be used to specify a fixed (unformatted) string or, when used in conjunction with `<label-param>`, can be used to build a formatted label. The label-param parameter accepts these macros:

- action.name - the first label set on the action
- workspace.name - resolves to workspace.shortLabel
- project.name - resolves to project.shortLabel
- element.name - resolves to element.shortLabel
- node.name - resolves to context.getNode().shortLabel

### 3.1 Trigger Actions, Rules and Controllers Behavior

For trigger actions whose owning extension is not yet initialized, the action is enabled based only on the registered rules; the controller's update method is not called until the extension has been initialized.

In the case that an action is invoked and the registered controller's update method has not been called, we will explicitly call the controller's update method before calling handleEvent. There are 3 expected outcomes in this situation:

1. Controller.update() returns true, handleEvent is called.
2. Controller.update() returns true, but disables the action. In this case, you must implement TriggerController to inform the user why the action cannot be performed, handleEvent is not called.
3. Controller.update() returns true, handleEvent() is called and steps the user through the process of setting up a context where the action can be performed.

### 3.2 When to use an oracle.ide.controller.TriggerController

If you cannot accurately disable an action using declarative rules, such that the action is enabled when it cannot be safely invoked, you should implement TriggerController and let the user know why the action cannot be performed in the current context.

**Availability:** Available

**XSD:** controllers-hook.xsd.

## 4. Menus

**Name:** menus 

**Description:** Adds menus and menu items to the main menu bar. 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest">
    ...
    <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
         <menu-hook xmlns="http://jcp.org/jsr/198/extension-manifest">
           <menus>
             <!-- 'javax.ide.view.MAIN_WINDOW_MENUBAR_ID' identifies
                  JDeveloper's main menu bar. -->
             <menubar id="javax.ide.view.MAIN_WINDOW_MENUBAR_ID">
               <!-- Adds trigger menu items to my custom menu. -->
               <menu id="MY_CUSTOM_MENU">
                 <!-- 'weight' is used to control where the section appears in
                      the menu. Weights are sorted in ascending order, each unique
                      value is its own section. -->
                 <section id="MY_CUSTOM_MENU_SECTION_ONE" weight="1.0">
                   <!-- For menu items and submenus, weight determines the
                        order within a menu section. Items are sorted in ascending
                        order or in the order they are added if the weight is not
                        unique. -->
                   <item action-ref="myTriggerActionId" weight="1.0" />
                   <item action-ref="myOtherTriggerActionId" weight="2.0" />
                 </section>
                 <!-- 'alphasort' is used to make a menu section sort its contents
                      alphabetically by label, rather than by assigned weight.
                      Weights are ignored if the menu item or submenu resides within
                      a section that is marked for alphabetical sorting. -->
                 <section id="MY_SORTED_MENU_SECTION" weight="2.0" alphasort="true">
                   <item action-ref="someActionId" weight="10.0" />
                   <!-- 'defaultsection' defines a section where items will be added
                        to this menu if no section weight is defined for an item.
                        This allows the menu owner to define a reasonable location
                        for extra items to be located. Notice too that the weight of
                        the submenu is less than that of the item defined first. It
                        will appear before the menu item. -->
                   <menu id="MY_SUBMENU" weight="3.0" defaultsection="1000.0">
                 </section>
               </menu>
             </menubar>
           </menus>
         </menu-hook>
     </triggers>
   </trigger-hooks>
 </extension>
 ```

**Availability:** Available

**XSD:** extension.xsd.

### 4.1 FCP-supplied menu ids are:

- javax.ide.FILE_MENU_ID
- javax.ide.EDIT_MENU_ID
- javax.ide.VIEW_MENU_ID
- javax.ide.HELP_MENU_ID

### 4.2 FCP-supplied sections are:

- javax.ide.OPEN_SECTION_ID
- javax.ide.NEW_SECTION_ID
- javax.ide.CLOSE_SECTION_ID
- javax.ide.PRINT_SECTION_ID
- javax.ide.SAVE_SECTION_ID
- javax.ide.COPY_PASTE_SECTION_ID
- menu-addin-section-id

### 4.3 JDeveloper menu IDs

Constants for many of JDeveloper's standard menu ids are defined in oracle.ide.IdeMainWindow.

### 4.4 Assigning weight and section values to menu and submenu items

The `weight` attribute allows you to specify a value for section weights and item weights. The weight is a float value which is stored as a client property in the menu item object. Weights are used to sort items in a menu in ascending order. For sections, each unique weight value identifies a new section, and again, the sections are ordered in ascending order.

If the weight attribute is not specified for a section, items within the section are added to the section defined by the menu as the location to add undefined items. The default section can be defined for a menu using the 'defaultsection' attribute.

Run JDeveloper with this command line flag: `-J-Dide.IdeAction.debug=true`. When this flag is used, the weight and section values are shown in the menu item's tooltip.

## 5. Context Menus

**Name:** context-menu-hook 

**Description:** Adds menu items to the context menus of editor, navigator or explorer. 

**Usage Example:**

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
     <context-menu-hook rule="context-has-java-source-node">
       <site idref="navigator"/>
       <extension-listener class="my.package.MyContextMenuListener"/>
       <menu>
         <!-- 'weight' is used to control where the section appears in
              the menu. Weights are sorted in ascending order, each unique
              value is its own section. -->
         <!-- section element must be in namespace
              http://jcp.org/jsr/198/extension-manifest. -->
         <section xmlns="http://jcp.org/jsr/198/extension-manifest" id="MY_CUSTOM_MENU_SECTION_ONE" weight="1.0">
           <!-- For menu items and submenus, weight determines the
                order within a menu section. Items are sorted in ascending
                order or in the order they are added if the weight is not
                unique. -->
           <item action-ref="myTriggerActionId" weight="1.0" />
           <item action-ref="myOtherTriggerActionId" weight="2.0" />
         </section>
       </menu>
     </context-menu-hook>
   </triggers>
 </trigger-hooks>
 ```

 **Availability:** Available

 **XSD:** context-menu-hook.xsd in ide. The `<site>` `idref` identifies the context menu(s) to which the menu items are to be inserted. If more than one site is specified, the site ids must be separated by commas. Sites are defined by the views themselves. Sites defined by the IDE are: `navigator`, `explorer` and `editor`. Two additional sites are rescat and palette; any extension's views can define sites for access to its context menus.

The `<extension-listener>` element is optional; if it is not used, the context-menu-hook operates as described below, whether or not the extension is loaded. See the Extension Listener section below for more information.

Each context-menu-hook must be associated with a rule. The menu items will will be inserted into the view's context menu by the context-menu-hook listener if and only if the rule evaluates to true. The value of the 'rule' attribute identifies what rule to evaluate.

A rule, including context-has-view, which is implied by the site id, need not be repeated as a rule in the controller for the action; instead, use the always-enabled rule in the controller. This will ensure that context menu items only appear when they are enabled.

The outer `<menu>` element is the context menu being added to; no menu identifier is necessary. The outer `<menu>` element may have only `<section>` child elements.

### 5.1 Extension Listener

The optional `<extension-listener>` element names a class defined in the extension that implements oracle.ide.controller.ContextMenuListener. The listener class must be public and have a null constructor. An extension using this feature should not explicitly add the listener to the same context menu in its initialization method, as this might result in more than one instance of the listener being called for the same menu.

The `<insert-when>` rules are always tested. If true, and the extension is loaded, listener calls are forwarded to the extension listener; if the extension is not loaded and the call is `menuWillShow` the `<menu>` items are inserted. This is the logic for `menuWillShow`:

```java
 if (insertWhenRulesMatch()) {
   if (hasExtensionListener() && extensionIsLoaded())
     forwardToExtensionListener();
   else
     addMenuSection();
 }
 ```

 Therefore, when it is called, the extension listener is responsible for adding any menus or items. The listener not limited to doing what the context-menu-hook listener does; it need not add the same menu items and may add other menu items.

 ## 6. Editor Menu

 **Name:** editor-menu-hook 
 
 **Description:** Adds menu items to the editor menu, e.g., Source menu. 
 
 **Usage Example:**

 ```xml
  <trigger-hooks>
   <triggers>
     <editor-menu-hook>
       <insert-when>
         <and>
           <rule type="context-has-project"/>
           <or>
             <!-- Simple rule format for rules with just one param -->
             <rule type="context-has-node" name="node-class" value="oracle.bali.xml.addin.XmlSourceNode"/>
             <!-- More typing is allowed, as well -->
             <rule type="context-has-node">
               <parameters>
                 <param name="node-class" value="="oracle.jdeveloper.model.JavaSourceNode"/>
               </parameters>
             </rule>
           </or>
         </and>
       </insert-when>
       <menu>
         <!-- 'weight' is used to control where the section appears in
              the menu. Weights are sorted in ascending order, each unique
              value is its own section. -->
         <!-- section element must be in namespace
              http://jcp.org/jsr/198/extension-manifest. -->
         <section xmlns="http://jcp.org/jsr/198/extension-manifest" id="MY_CUSTOM_MENU_SECTION_ONE" weight="1.0">
           <!-- For menu items and submenus, weight determines the
                order within a menu section. Items are sorted in ascending
                order or in the order they are added if the weight is not
                unique. -->
           <item action-ref="myTriggerActionId" weight="1.0" />
           <item action-ref="myOtherTriggerActionId" weight="2.0" />
         </section>
       </menu>
     </editor-menu-hook>
   </trigger>
 </trigger-hooks>
 ```

 **Availability:** Available

**XSD:** editor-menu-hook.xsd in ide. `<insert-when>` behaves the same as in `<context-menu-hook>` except that the rules are only evaluated when an editor is active.

The currently available rules are:

- `context-has-view` matches the node in the context to the node class. This rule expects a `view-class` parameter.
- `context-has-node` matches the node in the context to the node class. This rule expects a `node-class` parameter.

`<menu>` behaves the same as in `<context-menu-hook>` except that the menu added to is always the editor-specific menu, e.g., Source.

At least one positive `context-has-view` rule is required, to identify a specific editor whose menu is to be added to. More than one rule is allowed.

(A rule not enclosed in a `<not>` element or enclosed within even numbers of `<not>` elements is a positive rule. In other words, you cannot say "Apply this to any editor view except X," but you can say "Apply this to editor view X unless if it is an instance of Y.")

The use of any rules other than `context-has-view` or `context-has-node` in an editor menu is questionable. Unlike context menus, where (usually) only enabled menu items appear, menubar menus are expected to be relatively static, with disabled items when not applicable. Use the controller update-rules to specify enabling/disabling a menu item based on dynamic states.

### 6.1 Use as a standard (non-trigger) hook

`editor-menu-hook` may also be used as a non-trigger hook.

**Usage Example:**

```xml
 <extension ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       ...
       <actions xmlns="http://xmlns.oracle.com/jdeveloper/1013/extension">
         <action id="HProfileStartTimeCommand">
           <properties>
             <property name="Name" rskey="PROFILE_START_TIME_MENUITEM"/>
             <property name="MnemonicKey" rskey="PROFILE_START_TIME_MENUITEM_MNEMONIC" />
             <property name="SmallIcon">${OracleIcons.PROFILER_CPU_PROFILE}</property>
           </properties>
         </action>
       </actions>
       <controllers xmlns="http://xmlns.oracle.com/ide/extension">
         <controller class="oracle.jdevimpl.profiler.ProfilerController">
           <update-rules>
             <update-rule rule="context-has-project">
               <action id="HProfileStartTimeCommand"/>
             </update-rule>
           </update-rules>
         </controller>
       </controllers>
       ...
     </triggers>
   </trigger-hooks>
   <hooks>
     <editor-menu-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <insert-when>
           <rule type="context-has-view" name="view-class" value="oracle.ide.ceditor.CodeEditor"/>
         </insert-when>
         <menu>
           <section xmlns="http://jcp.org/jsr/198/extension-manifest" id="oracle.jdeveloper.TB_RUN_DEBUG_SECTION" weight="5.0">
             <item action-ref="HProfileStartTimeCommand"/>
           </section>
         </menu>
     </editor-menu-hook>
   </hooks>
 </extension>
```

An editor-menu-hook can be moved from trigger-hooks to hooks with no more change than adding `xmlns="http://xmlns.oracle.com/ide/extension"` (required). The hook can refer to actions defined in trigger-hooks.

As a hook, the menu items will not be added until the extension is loaded.

## 7. Accelerators/Shortcut Keys

**Name:** accelerator-hook 

**Description:** Registers files that contain accelerator key definitions for actions registered by the extension 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest">
    ...
    <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
         <accelerator-hook>
           <file>oracle/jdevimpl/help/accelerators.xml</file>
           <file>oracle/jdevimpl/help/localaccelerators.xml</file>
         </accelerator-hook>
     </triggers>
   </trigger-hooks>
 </extension>
```

NOTE: If you switch from programmatic to declarative registration of accelerator files, delete your system directory before testing. If not, the IDE will use your programmatic one and not use your declarative one, leaving you wondering what's wrong.

**Availability:** Available

**XSD:** accelerator-hook.xsd

## 8. Gallery Items

**Name:** gallery 

**Description:** Registers gallery folders and gallery items. 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       <gallery xmlns="http://xmlns.oracle.com/jdeveloper/1013/extension">
         <folders>
           <!-- The 'label' attribute allows the folder name to be translated; the key
                must be in the extension manifest resource bundle. -->
           <name label="${GALLERY_FOLDER_PROJECTS}">Projects</name>
           <category>General</category>
         </folders>
         <item rule="always-enabled">
           <!-- name is the fully-qualified name of an oracle.ide.wizard.Invokable  -->
           <name>oracle.jdeveloper.template.wizard.TemplateWizard</name>
           <!-- id is optional, and if specified will be stored in the Context
                so it can be retrieved when the wizard is invoked. -->
           <id>Application</id>
           <!-- description is the short label of the gallery item -->
           <description>${NEW_APPLICATION_TEMPLATE_GALLERY_ITEM}</description>
           <!-- help is the long label for the gallery item -->
           <help>${MANAGE_TEMPLATES_WIZARD_DESCRIPTION}</help>
           <folder>Applications</folder>
           <technologyKey>General</technologyKey>
           <icon>/oracle/javatools/icons/apptemplate.jpg</icon>
         </item>
       </gallery>
     </triggers>
   </trigger-hooks>
   ...
 </extension>
 ```

 The above example shows the use of the new id element which is used to invoke a wizard by its id. The related Wizard.invoke(context) code looks like:

 ```java
 String id = Wizard.getWizardId(context);
 if (oracle.ide.model.Workspace.DATA_KEY.equals(id) {
   ...
 }
 ```

 Each gallery `<item>` has a required attribute 'rule'. The value of the 'rule' attribute identifies what rule to evaluate to decide whether or not the gallery item should be enabled.

**Some Common Rules**

Most of our existing wizards are enabled in the gallery if the context has a project or has a workspace, and some items are always enabled. There are Rule implementations for each of these. To use in the extension.xml, use one of these IDs:

```xml
 <item rule="always-enabled">...</item>
 <item rule="context-has-project">...</item>
 <item rule="context-has-workspace">...</item>
```

**Availability:** Available

**XSD:** idehook.xsd

## 9. Technology Scopes

**Name:** technology-hook 

**Description:** Registers a technology that augments the base IDE Technology definitions. 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       <technology-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <technology>
         <key>Java</key>
         <name>${JAVA_TECH}</name>
         <description>${JAVA_TECH_DESC}</description>
         <wizard-pages>
             <page>
             <traversable-class>oracle.jdevimpl.wizard.project.JavaTechnologyPanel</traversable-class>
             </page>
         </wizard-pages>
         </technology>
         <technology>
             <key>Swing/AWT</key>
             <name>${SWING_AWT_TECH}</name>
             <description>${SWING_AWT_TECH_DESC}</description>
             <technology-dependencies>
             <key>Java</key>
             </technology-dependencies>
         <technology>
       </technology-hook>
     </triggers>
   </trigger-hooks>
   ...
 </extension>
```

**Availability:** Available

**XSD:** technology-hook.xsd

## 10. Editors

***Name:** editors 

**Description:** Register editors. 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       <editors>
         <editor id="SwingDesigner"
                 editor-class="oracle.jdevimpl.uieditor.UIEditorImpl"
                 label="${EDITOR_LABEL}"
                 default="false"
                 duplicable="false">
           <node-type class="oracle.jdeveloper.model.JavaSourceNode"/>
         </editor>
         <dynamic-editor
                 id="HistoryViewer"
                 rule="context-has-local-history"
                 editor-class="oracle.jdevimpl.history.HistoryViewer"
                 weight="0.4"
                 label="${EDITOR_LABEL}"
                 default="false"
                 duplicable="false" />
       </editors>
     </triggers>
   </trigger-hooks>
   ...
 </extension>
 ```

 ## 11. NodeFactory Recognizers

 **Name:** node-recognizers-hook 

 **Description:** Register node recognizing rules. 
 
 **Usage Example:**

 ```xml
  <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       <node-recognizers-hook xmlns="http://xmlns.oracle.com/ide/extension">
           <node-conversions>
             <node-conversion>
               <old-type>oracle.jdeveloper.model.JspSourceNode</old-type>
               <new-type>oracle.jdevimpl.webapp.html.HtmlSourceNode</new-type>
             </node-conversion>
           </node-conversions>
           <url-recognizer>
               <protocol>
                 <protocol>xss</protocol>
                 <node-type>oracle.xss.XssSourceNode</node-type>
               </protocol>
               <file-extension>
                 <extension>.java</extension>
                 <node-type>oracle.jdeveloper.model.JavaSourceNode</node-type>
               </file-extension>
               <file-content-patterns>
                 <pattern>/**ADFFaces_Skin_File / DO NOT REMOVE**/</pattern>
               <file-content-patterns>
           </url-recognizer>
           <xml-recognizer>
               <namespace>
                 <uri>http://myfaces.apache.org/trinidad/skin</uri>
                 <elem-name>skins</elem-name>
                 <node-type>oracle.jdevimpl.webapp.jsp.taglibraries.trinidad.skins.TrinidadSkinsNode</node-type>
               </namespace>
               <schema>
                 <!-- location has to be a URI -->
                 <location>http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd</location>
                 <root-elem>persistence</root-elem>
                 <node-type>oracle.toplink.workbench.addin.node.PersistenceNode</node-type>
               </schema>
               <doctype>
                 <public-id>-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN</public-id>
                 <system-id>http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd</system-id>
                 <node-type>oracle.jdeveloper.xml.j2ee.tld.TagLibNode</node-type>
               </doctype>
               <root-elem>
                 <elem-name>logging_configuration</elem-name>
                 <node-type>ADFLoggingNode</node-type>
               </root-elem>
           </xml-recognizer>
       </node-recognizers-hook>
     </triggers>
   </trigger-hooks>
   ...
 </extension>
 ```

**Availability:** Available

**XSD:** node-recognizer-hook.xsd

## 12. IDE Preferences/Settings

**Name:** settings-ui-hook 

**Description:** Register Settings UI panel 

**Usage description:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
   ...
   <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
       <settings-ui-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <page id="CoolFeaturePrefs" parent-idref="/preferences">
           <label>${SOME_RES_KEY}</label>
           <tooltip>${SOME_RES_KEY}</tooltip>
           <traversable-class>oracle.killerapp.coolfeature.CoolFeaturePrefsPanel</traversable-class>
         </page>
       </settings-ui-hook>
     </triggers>
   </trigger-hooks>
   ...
 </extension>
 ```

**Availability:** Available.

**XSD:** settings-ui-hook.xsd

## 13. Application Preferences/Settings

Same as IDE Preferences/Settings.

## 14. Project Preferences/Settings

Same as IDE Preferences/Settings. Project Preferences relate to ContentSetProviders.

Sample from adfmcoredt-ide

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
     <triggers>
      <settings-ui-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <page id="DbPanelSettings" parent-idref="/preferences">
           <label>${DBPANELSETTINGS}</label>
           <tooltip>${DBPANELSETTINGS}</tooltip>
           <traversable-class>oracle.adfdtinternal.model.ide.dbpanel.settings.DbPanelSettingsPanel</traversable-class>
         </page>
       </settings-ui-hook>
       <settings-ui-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <page id="ADFmSettings" parent-idref="/Project">
           <label>${ADFMSETTINGS}</label>
           <tooltip>${ADFMSETTINGS}</tooltip>
           <traversable-class>oracle.adfdtinternal.model.ide.settings.ADFMSettingsPanel</traversable-class>
         </page>
       </settings-ui-hook>
       <settings-ui-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <page id="ADFmRegisteredRules" parent-idref="/Project/ADFmSettings">
           <label>${REGISTERED_RULES}</label>
           <tooltip>${REGISTERED_RULES}</tooltip>
           <traversable-class>oracle.jbo.dt.validation.RegisteredRulesPanel</traversable-class>
         </page>
       </settings-ui-hook>
````

## 15. Content Set Providers

**Name:** content-set-providers-hook 

**Description:** Enables registration of lazily instantiated content set providers. 

**Usage Example:**

```xml
 <content-set-providers-hook xmlns="http://xmlns.oracle.com/ide/extension">
   <content-set-provider>
     <label>Application Sources</label>
     <navigable-label>Java Paths</navigable-label>
     <key>oracle.jdeveloper.model.PathsConfiguration/javaContentSet</key>
     <class>oracle.jdeveloper.model.JavaContentSetProvider</class>
     <display-folders-as-packages>true</display-folders-as-packages>
     <can-contain-java-sources>true</can-contain-java-sources>
     <flat-level-enabled>true</flat-level-enabled>
     <application-level-content>false</application-level-content>
   </content-set-provider>
 </content-set-providers-hook>
```

**Availability:** Available

**XSD:** content-set-providers.hook.xsd

## 16. Singleton Registration

**Name:** singleton-provider-hook 

**Description:** Enables registration of lazily instantiated singletons. 

**Usage Example:**

```xml
 <?xml version = '1.0' encoding = 'UTF-8'?>
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest"
            id="com.acme.myid"
            esdk-version="1.0"
            version="11.1.1">
    <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
       <triggers>
          <singleton-provider-hook>
             <singleton headless="false"
                        base-class="com.acme.MyService"
                        impl-class="com.acme.impl.MyServiceImpl" />
          </singleton-provider-hook>
       </triggers>
    </trigger-hooks>
 </extension>
```

**Availability:** Available

**XSD:** service-provider-hook.xsd

**Use Case:** The use of a registered singleton pattern is very common in several areas of the IDE, for example LogManager.getLogManager() or ExtensionRegistry.getExtensionRegistry(). Several extensions implement this pattern by using JNDI and a pair of static methods like this:

On extension `oracle.ide` (let's call it `A`)

```java
 public abstract class LogManager {
   public static LogManager getLogManager()
   {
     LogManager logManager = (LogManager) Names.lookup(Names.newInitialContext(), LOG_MANAGER_NAME);
     if( logManager == null )
     {
       logManager = new BasicLogManager();
     }
     return logManager;
   }
````

```java
   public static void setLogManager( LogManager logManager )
   {
     Names.bind( Names.newInitialContext(), LOG_MANAGER_NAME, logManager );
   }
 }
```

On extension oracle.ideimpl (let's call it `B`) which has a strict dependency on oracle.ide:

```java
 class LogManagerImpl {
   LogManagerImpl() {
      LogManager.setLogManager(this);
   }
 }
```

The problem comes when a third extension like oracle.ide.feedbackmanager (let's call it C) tries to use a LogManager (from oracle.ide) but oracle.ideimpl has not been initialized yet for some reason. There can't be a hard dependency from B to A to have LogManagerImpl be initialized and set before C tries to access it. Another mechanism is needed and it has to be declarative too. This is the reason for the introduction of a new hook: the singleton-provider-hook.

This hook will feed data to oracle.ide.SingletonProvider (package and class name subject to change) which will keep a record of all singletons by base-class/impl-class and extension id. SingletonProvider will expose a single method to extension writers to retrieve a singleton instance from the cache. The important thing to remember here is that the extension may have not been initialized yet (remember we're using declarative registration), which means this method will check with the ExtensionRegistry to determine if the extension needs to be initialized or not. Hence why this hook is also a trigger hook.

The code now becomes:

On extension `oracle.ide`

```java
 public abstract class LogManager {
   public static LogManager getLogManager()
   {
     LogManager logManager = SingletonProvider.find(LogManager.class);
     if( logManager == null )
     {
       logManager = new BasicLogManager();
     }
     return logManager;
   }
 }
```

On extension `oracle.ideimpl``

```java
 class LogManagerImpl {
   LogManagerImpl() {
   }
 }
```

On `oracle.ideimpl`'s extension manifest

```xml
 <?xml version = '1.0' encoding = 'UTF-8'?>
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest"
            id="oracle.ideimpl"
            esdk-version="1.0"
            version="11.1.1">
    <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
       <triggers>
          <singleton-provider-hook>
              <singleton base-class="oracle.ide.log.LogManager"
                         impl-class="oracle.ideimpl.log.LogManagerImpl" />
          </singleton-provider-hook>
       </triggers>
    </trigger-hooks>
 </extension>
```

**Note:** the `headless` attribute in the singleton-provider-hook is optional and if left out will be treated as true. This attribute is consulted when an instance of the singleton needs to be created. When running in headless mode, a call to SingletonProvider.find() will return null for a singleton registered with headless="false".

## 17. Application and Project Migrators

**Name:** migrator-trigger-hook 

**Description:** Register a Migrator. 

**Usage Example:**

```xml
 <extension xmlns="http://jcp.org/jsr/198/extension-manifest" ...>
 ...
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
     <node-migrator-hook>
       <application-migrators>
         <migrator>
           <key>OfflineTransferMigrator</key>
           <version>11.1.1.1.0</version>
           <class>oracle.jdevimpl.offlinedb.migration.OfflineTransferMigrator</class>
         </migrator>
       </application-migrators>
       <project-migrators>
         <migrator>
           <key>PageDefinitionParameterValueMigrator</key>
           <version>11.1.1.1.0.5</version>
           <class>oracle.adfdtinternal.model.ide.xmled.migration.PageDefinitionParameterValueMigrator</class>
         </migrator>
       </project-migrators>
     </node-migrator-hook>
   </triggers>
 </trigger-hooks>
 ...
 </extension>
```

**Availability:** Available

**XSD:** migrator-trigger-hook.xsd 

**Notes:** version attribute can be omitted, if so the the default from NodeMigratorHelper will be used. This means that it will work exactly as programmatic registration of NodeMigrationHeper subclass which does NOT override the getNodeMigratorHelperVersion() method. For more information see the Javadoc for NodeMigratorHelper.getNodeMigratorHelperVersion()

## 18. URLFileSystem Hook

**Name:** urlfilesystem-hook 

**Description:** Register a URLFileSystemHelper, URLFileSystemHelperDectorator, or URLStreamHandlerFactory with the URLFileSystem.

**Usage Example:** Here is an excerpt from the vfs module extension manifest, which registers a URLFileSystemHelperDecorator for the "file" protocol.

```xml
 <urlfilesystem-hook xmlns="http://xmlns.oracle.com/jdeveloper/hooks/urlfilesystem">
   <fsdecorator protocol="file" class="oracle.ide.net.VirtualFileSystemHelper" />
 </urlfilesystem-hook>
```

**Availability:** Available.

**XSD:** urlfilesystem-hook.xsd

Extension initialization will be triggered when the helper, decorator, or stream handler factory is needed. For handlers and decorators, this is when the corresponding protocol is used.

## 19. ImportExport Hook

**Name:** importexport-hook 

**Description:** Registers an import or export wizard with the import/export framework 

**Usage Example:** Here is an excerpt from the vcs-dimensions module extension manifest

```xml
 <importexport-hook xmlns="http://xmlns.oracle.com/ide/1013/ide-importexport">
 <import class="oracle.jdevimpl.vcs.dimensions.DMImportExportWizard"
 name="${IMPORT_WZ_TITLE}"
 tooltip="${IMPORT_WZ_TOOLTIP}">
 </import>
 <export class="oracle.jdevimpl.vcs.dimensions.DMImportExportWizard"
 name="${EXPORT_WZ_TITLE}"
 tooltip="${EXPORT_WZ_TOOLTIP}">
 </export>
 </importexport-hook>
```

**Availability:* Available

**XSD:** importexport-hook.xsd

**Note:** The importexport-hook must NOT be used in a conditional section of the triggers-hook

## 20. Product hook

**Name:** product-hook 

**Description:** Registers product information used to brand the product including providing information to the about box Usage Example: Here is an excerpt from the jdeveloper-extras module extension manifest

```xml
 <product-hook xmlns="http://xmlns.oracle.com/ide/extension"
           name="Oracle JDeveloper 11g Development Build" icon="res:${PRODUCT_ICON}"
           short-name="JDeveloper"
           edition-name="Java Edition"
           welcome-page="uri:../doc/welcome-java/welcome.html"
           bugdb-prod-id="807" bugdb-comp-id="JDEV"
           feedback-server="cdandoy-www.us.oracle.com" feedback-port="4656">
           <!-- Do Not translate the copyright message. It needs to be changed
                even when WPTG have locked down translations. -->
          <about-box
            copyright="Copyright &#x00A9; 1997, 2010 Oracle and/or its affiliates. All rights reserved."
            image="${OracleIcons.HEADER_JDEVELOPER}"
            banner-image="res:${PRODUCT_ABOUT_BANNER_IMAGE}"/>
          <file-association suffix=".jws" label="${JDEV_WORKSPACE}" regkey="JDeveloper.jwsFile"/>
          <file-association suffix=".jpr" label="${JDEV_PROJECT}" regkey="JDeveloper.jprFile"/>
          <file-association suffix=".java" label="${JAVA_SOURCE_FILE}" regkey="JDeveloper.javaFile"/>
          <file-association suffix=".jsp" label="${JAVA_SERVER_PAGE}" regkey="JDeveloper.jspFile"/>
          <additional-icons>
             <icon>res:${PRODUCT_ICON_48}</icon>
             <icon>res:${PRODUCT_ICON_64}</icon>
          </additional-icons>
       </product-hook>
```

**Availablilty:** Available

**XSD:** product-hook.xsd

**Note:** The product-hook must NOT be used in a conditional section of the triggers-hook

## 21. Menu Customizations hook

**Name:** menu-customizations 

**Description:** Registers information about menus that are to be hidden 

**Usage Example:**

```xml
 <hooks>
   <menu-customizations xmlns="http://xmlns.oracle.com/ide/customization">
     <action idref="ObjectGalleryCommand" unavailable="true">
       <hidden>false</hidden>
     </action>
     <action idref="Ide.OPEN_CMD_ID">
       <hidden>true</hidden>
     <action>
   </menu-customizations>
 </hooks>
```

**Availablilty:** Available

**XSD:** menu-customizations-hook.xsd

**Note:** that if the action's hidden element is true, it will not show up in the menubar at all.

Actions marked as unavailable are disabled and none of the controllers attached to the action are called from update() or handleEvent() methods.

The menucustomizations-hook must NOT be used in a conditional section of the triggers-hook

## 22. Bridge Extensions

A "bridge" extension connects two related technologies. For example, the ADF View Databinding extension connects ADF View and ADF Model. A bridge extension needs to be loaded whenever the core extensions of both related technologies are loaded. In some cases, the bridge extensions don't have natural triggers.

We've added support in the IDE framework for declaring an extension to be a bridge extension in extension.xml, and the ExtensionRegistry uses that information to automatically load the bridge extension at the appropriate time.

Say you've got extensions A and B that represent two related technologies, and you've got extension C that is the bridge between those technologies. Extension C depends on both A and B.

If (and only if!) there was no natural trigger for extension C, then you'd put this in the triggers section of C's extension.xml:

```xml
 <bridge-extension-hook bridge-id="C-bridge"
                        from-extension="A"
                        to-extension="B" />
```

Both A and B have to agree to this relationship, so in both A and B's extension.xml you'd have to put this in the trigger section:

```xml
 <bridge-endpoint-hook bridge-id="C-bridge" />
```

Whenever A and B are both fully loaded, the ExtensionRegistry will automatically fully load C.

**Availablilty:** Available 

**XSDs:** bridge-extension-hook.xsd, bridge-endpoint-hook.xsd

## 23. On Project Open hook

**Name:** on-project-open-hook 

**Description:** Allows an extension to inspect project data to determine what extensions need to be initialized when a project is opened. 

**Usage Example:**

```xml
 <on-project-open-hook>
   <data-handler class="client.package.ClientDataHandler"/>
   <hash-structure key="oracle.ide.model.TechnologyScopeConfiguration"/>
 </on-project-open-hook>
```
`<data-handler>` identifies a ProjectDataHandler subclass to be called if a project with a matching hash-structure key is opened.

`<hash-structure>` provides the key of a HashStructure that may appear in a project.jpr file.

**Availablilty:** Available

**XSD:** on-project-open-hook.xsd

## 24. Help Callbacks Hook

**Name:** help-callbacks-hook 

**Description:** Registers classes necessary for help callbacks to work. Help callbacks are used in the help content to allow the help system to include links that invoke dialogs, wizards, or actions. In order for this to work the actual classes used need to be registered so that the help system has the necessary information to create instances of the objects and use them. 

**Usage Example:**

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
     <help-callbacks-hook>
       <callback id="oracle.jdevimpl.wizard.project.NewApplicationWizard"
                class="oracle.jdevimpl.wizard.project.NewApplicationWizard" />
     </help-callbacks-hook>
   </triggers
 </trigger-hooks>
```

**Availability:** Available

**XSD:** help-callbacks-hook.xsd

## 25. Help Hook

**Name:** help Description: Registers help content with the help system 

**Usage Example:**

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
     <help xmlns="http://xmlns.oracle.com/jdeveloper/1013/extension">
       <item>
           <helpName>ejb</helpName>
           <helpURL>../doc/$edition/ohj/ejb.jar!/ejb.hs</helpURL>
           <relativeTo>developing_with_tiles</relativeTo>
         <relativePosition>after</relativePosition>
       </item>
     </help>
   </triggers>
 </trigger-hooks>
```

**Availability:** Available

**XSD:** help.xsd

## 26. Dockable Hook

**Name:** dockable-factory-hook/dockable-hook 

**Description:** Registers a dockable factory with the IDE windowing system. 

**Usage Example:**

### 26.1 Standard Dockable Factory

The dockable factory is defined in the trigger-hooks 'dockable-factory-hook'. This will cause the dockable factory to be installed when one of its dockables is requested e.g.: via View-><Dockable>

A separate dockable-hook can also be supplied to run the dockable factory install when the extension defining the hook is loaded by something other than its trigger hook. This is useful, for example, extensions that want to display a window as soon as the extension is loaded.

It is usual to provide both a dockable-factory-hook and a dockable-hook.

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
       <dockable-factory-hook xmlns="http://xmlns.oracle.com/ide/extension">
         <factory id="ToDoAddin" class="oracle.jdevimpl.todo.ToDoDockableFactory"/>
       </dockable-factory-hook>
   </triggers>
 </trigger-hooks>
 <hooks>
   <dockable-hook  xmlns="http://xmlns.oracle.com/ide/extension">
       <dockable id="ToDoAddin"/>
   </dockable-hook>
 </hooks>
```

### 26.2 Drawer Dockable

A drawer dockable is a dockable hosted inside another, such as the 'Recently Opened Files' drawer in the 'Application Navigator'. It needs an extra regular 'hosted-dockable-hook' hook describing the relationship between the hosted and host dockables.

```
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
         <factory id="Applicationoracle_ideimpl_editor_RecentFiles_VIEW_TYPE"
                  class="oracle.ideimpl.editor.RecentFilesDockableFactory"/>
   </triggers>
 </trigger-hooks>
 <hooks>
   <dockable-hook  xmlns="http://xmlns.oracle.com/ide/extension">
       <dockable id="Applicationoracle_ideimpl_editor_RecentFiles_VIEW_TYPE"/>
   </dockable-hook>
   <hosted-dockable-hook xmlns="http://xmlns.oracle.com/ide/extension">
       <hosted-dockable view-id="Applicationoracle_ideimpl_editor_RecentFiles_VIEW_TYPE.Applicationoracle_ideimpl_editor_RecentFiles_VIEW_NAME"
                                           host-id="ApplicationNavigatorWindow.ApplicationNavigatorName"
                                           weight="10"/>
     </hosted-dockable-hook>
 <hooks>
```

**Availability:** Available

**XSD:** dockable-hook.xsd

**Note:** For the hosted element, the view ids of the dockables, rather than the id of dockable factories, are used. This is because a dockable factory can create many dockables.

## 27. Historian Hook

**Name:** historian-hook 

**Description:** Allows a Historian to be registered for local history. 

**Usage Example:**

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
     <historian-hook xmlns="http://xmlns.oracle.com/ide/extension">
       <historian-class>oracle.ide.model.TextNodeHistorian</historian-class>
       <node-class>oracle.ide.model.TextNode</node-class>
     </historian-hook>
   </triggers>
 </trigger-hooks>
```

This hook is equivalent to the HistoryManager.registerHistorian() API. This means that only the given node class will be associated with the Historian, and derivatives of the node class must be individually registered.

Note that TextNodeHistorian registrations are implicit for registered TextNode types associated with file extensions through the Recognizer API.

**Availability:** Available

**XSD:** historian-hook.xsd

## 28. External Tools Hook

**Name:** externaltools 

**Description:** Allows External Tool scanner, external tool type, and external tool macros to be registered. 

**Usage Example:**

```xml
 <trigger-hooks xmlns="http://xmlns.oracle.com/ide/extension">
   <triggers>
   <externaltools xmlns="http://xmlns.oracle.com/ide/extension">
      <macro macro="env" name="${ENVIRONMENT_NAME}"
             description="${ENVIRONMENT_DESCRIPTION}"
             isDirectoryMacro="true">oracle.ideimpl.externaltools.macro.EnvironmentVariable</macro>
      <scanner id="windowsimpl" name="${WINDOWS_SCANNER_NAME}"
               rule="os-is-windows"> oracle.ideimpl.externaltools.WindowsToolScanner</scanner>
      <externalToolType id="oracle.ideimpl.externaltools.program.ExternalProgramType" name="${PROGRAM_TYPE}"
                        description="${PROGRAM_TYPE_HINT}"
                        externalToolClass="oracle.ideimpl.externaltools.program.ExternalProgramTool"
                           >oracle.ideimpl.externaltools.program.ExternalProgramType</externalToolType>
     </externaltools>
   </triggers>
 </trigger-hooks
```

**Availability:** Available

**XSD:** externaltools-hook.xsd
