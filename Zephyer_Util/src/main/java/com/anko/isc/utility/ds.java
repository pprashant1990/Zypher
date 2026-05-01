package com.anko.isc.utility;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.fasterxml.jackson.databind.ObjectMapper;
import com.informatica.dsg.zapi.model.AutomationStatus;
import com.informatica.dsg.zapi.model.CascadeCustomField;
import com.informatica.dsg.zapi.model.Component;
import com.informatica.dsg.zapi.model.Issue;
import com.informatica.dsg.zapi.model.IssueObject;
import com.informatica.dsg.zapi.model.IssueType;
import com.informatica.dsg.zapi.model.Priority;
import com.informatica.dsg.zapi.model.Project;
import com.informatica.dsg.zapi.model.TestCategory;
import com.informatica.dsg.zapi.model.TestStep;
import com.informatica.dsg.zapi.model.TestSubCategory;
import com.informatica.dsg.zapi.model.TestType;
import com.informatica.dsg.zapi.model.User;
import com.informatica.dsg.zapi.model.Version;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureFileTagsReader {
    private LinkedHashMap<String, String> scenarioLevelTags;
    private LinkedHashMap<String, String> featureLevelTags;
    private LinkedHashMap<String, IssueObject> issues;
    private LinkedHashMap<String, String[]> data;
    private ArrayList<String> values;
    private LinkedHashMap<String, LinkedHashMap<String, String[]>> scenarioData;
    private LinkedHashMap<String, String> scenarioOutlineTags;
    private LinkedHashMap<String, String> scenarioOutlineTags1;
    private LinkedHashMap<Integer, String> examplesLines;
    PropertiesReader prop = new PropertiesReader();

    public FeatureFileTagsReader() {
    }

    public String readFile(String url) {
        String sb = "";
        ArrayList<String> examplesData = null;

        String sCurrentLine;
        try {
            for(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(url)); (sCurrentLine = lineNumberReader.readLine()) != null; sb = sb + sCurrentLine + "\n") {
                int lineNo = lineNumberReader.getLineNumber();
                String scenarioOutline;
                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.SCENARIO.toString())) {
                    scenarioOutline = (String)Files.readAllLines(Paths.get(url)).get(lineNo - 2);
                    this.scenarioLevelTags.put(sCurrentLine.trim(), scenarioOutline);
                    this.scenarioOutlineTags1.put(sCurrentLine.trim() + "$" + lineNo, scenarioOutline);
                }

                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.SCENARIO_OUTLINE.toString())) {
                    scenarioOutline = sCurrentLine;
                    String[] words = sCurrentLine.split(" ");
                    ArrayList<String> variables1 = new ArrayList();
                    int m = false;

                    for(int l = 0; l < words.length; ++l) {
                        if (words[l].startsWith("<")) {
                            variables1.add(words[l]);
                        }
                    }

                    String previousLineTags = (String)Files.readAllLines(Paths.get(url)).get(lineNo - 2);
                    String desc = "";
                    boolean headerLine = false;
                    int descIndex = false;
                    LinkedHashMap<String, Integer> index = null;
                    examplesData = new ArrayList();
                    this.scenarioOutlineTags1.put(sCurrentLine + "$" + lineNo, previousLineTags);
                    boolean exFlag = false;

                    while((desc = lineNumberReader.readLine()) != null && !desc.trim().startsWith("@")) {
                        int lineNo1 = lineNumberReader.getLineNumber();
                        String ex = desc;
                        if (desc.contains("Examples:")) {
                            exFlag = true;
                        }

                        try {
                            if (ex.trim().startsWith("|") && !ex.trim().contains(((String)variables1.get(0)).replaceAll("[<>]", "")) && exFlag) {
                                this.examplesLines.put(lineNo1, ex);
                            }

                            if (desc.contains("|") && desc.trim().contains(((String)variables1.get(0)).replaceAll("[<>]", ""))) {
                                index = this.getIndexofVariables(scenarioOutline, desc);
                            }
                        } catch (Exception var20) {
                            System.out.println("Scenario outline does not contain the variables");
                        }

                        if (desc.contains("@jiraKey") && desc.contains("|")) {
                            String[] str = desc.trim().split("\\|");
                            examplesData.add(desc);
                        }
                    }

                    this.data = this.getExamplesDataMap(examplesData, index);
                    this.scenarioData.put(scenarioOutline + "#" + lineNo, this.data);
                    this.scenarioOutlineTags.put(scenarioOutline, previousLineTags);
                }

                String[] assignee;
                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.COMPONENT.toString())) {
                    assignee = this.getTagsValue(sCurrentLine, FeatureFileTagsReader.TAGS.COMPONENT.toString() + "(\\S+)").split("_");
                    if (assignee.length > 1) {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.COMPONENT.toString(), assignee[1].trim().replaceAll("::", " "));
                    } else {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.COMPONENT.toString(), (Object)null);
                    }
                }

                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.SUB_COMPONENT.toString())) {
                    assignee = this.getTagsValue(sCurrentLine, FeatureFileTagsReader.TAGS.SUB_COMPONENT.toString() + "(\\S+)").split("_");
                    if (assignee.length > 1) {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.SUB_COMPONENT.toString(), assignee[1].trim().replaceAll("::", " "));
                    } else {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.SUB_COMPONENT.toString(), (Object)null);
                    }
                }

                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.ASSIGNEE.toString())) {
                    assignee = this.getTagsValue(sCurrentLine, FeatureFileTagsReader.TAGS.ASSIGNEE.toString() + "(\\w+)").split("_");
                    if (assignee.length > 1) {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.ASSIGNEE.toString(), assignee[1].trim());
                    } else {
                        this.featureLevelTags.put(FeatureFileTagsReader.TAGS.ASSIGNEE.toString(), (Object)null);
                    }
                }

                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.REPORTER.toString())) {
                    scenarioOutline = this.getTagsValue(sCurrentLine, FeatureFileTagsReader.TAGS.REPORTER.toString() + "(\\w+)").split("_")[1].trim();
                    this.featureLevelTags.put(FeatureFileTagsReader.TAGS.REPORTER.toString(), scenarioOutline);
                }

                if (sCurrentLine.contains(FeatureFileTagsReader.TAGS.TYPE.toString())) {
                    scenarioOutline = this.getTagsValue(sCurrentLine, FeatureFileTagsReader.TAGS.TYPE.toString() + "(\\w+)").split("_")[1].trim();
                    this.featureLevelTags.put(FeatureFileTagsReader.TAGS.TYPE.toString(), scenarioOutline);
                }
            }
        } catch (IOException var21) {
            var21.printStackTrace();
        }

        return sb;
    }

    String getTagsValue(String str, String prefix) {
        String matched = "";

        for(Matcher m = Pattern.compile(prefix).matcher(str); m.find(); matched = m.group()) {
        }

        return matched;
    }

    LinkedHashMap getIndexofVariables(String scenarioOutline, String desc) {
        String[] words = scenarioOutline.split(" ");
        LinkedHashMap<String, Integer> index = new LinkedHashMap();
        ArrayList<String> variables = new ArrayList();
        int m = false;

        for(int l = 0; l < words.length; ++l) {
            if (words[l].startsWith("<")) {
                variables.add(words[l]);
            }
        }

        boolean headerLine = false;
        String[] header = desc.split("\\|");
        String match = "";

        while(!headerLine) {
            for(int k = 0; k < header.length; ++k) {
                String header1 = header[k].trim();

                for(int i = 0; i < variables.size(); ++i) {
                    match = ((String)variables.get(i)).substring(1, ((String)variables.get(i)).length() - 1);
                    if (header[k].trim().contains(match)) {
                        index.put(header[k].trim(), k);
                        headerLine = true;
                    }
                }
            }
        }

        return index;
    }

    public LinkedHashMap<String, String[]> getExamplesDataMap(ArrayList<String> ex, LinkedHashMap index) {
        this.values = new ArrayList();
        this.data = new LinkedHashMap();
        String[] val = new String[ex.size()];
        if (index != null) {
            for(Iterator<Map.Entry<String, Integer>> iterator = index.entrySet().iterator(); iterator.hasNext(); val = new String[ex.size()]) {
                Map.Entry<String, Integer> element = (Map.Entry)iterator.next();
                String key = (String)element.getKey();
                int value = (Integer)element.getValue();

                for(int l = 0; l < ex.size(); ++l) {
                    String[] str = ((String)ex.get(l)).trim().split("\\|");
                    val[l] = str[value].trim();
                }

                this.data.put((String)element.getKey(), val);
            }
        }

        return this.data;
    }

    public void getAllScenarios() {
        ArrayList<String> scenarioText = new ArrayList();

        for(Iterator<Map.Entry<String, LinkedHashMap<String, String[]>>> iterator1 = this.scenarioData.entrySet().iterator(); iterator1.hasNext(); scenarioText.clear()) {
            String tags = "";
            Map.Entry<String, LinkedHashMap<String, String[]>> element = (Map.Entry)iterator1.next();
            if (this.scenarioOutlineTags.containsKey(((String)element.getKey()).replaceAll("#.*", ""))) {
                tags = (String)this.scenarioOutlineTags.get(((String)element.getKey()).replaceAll("#.*", ""));
            }

            String text = ((String)element.getKey()).replaceAll("#.*", "").replaceAll("[<>]", "");
            LinkedHashMap<String, String[]> map = (LinkedHashMap)element.getValue();
            Iterator<Map.Entry<String, String[]>> iterator2 = map.entrySet().iterator();
            int temp = 0;

            while(iterator2.hasNext()) {
                Map.Entry<String, String[]> mapelement = (Map.Entry)iterator2.next();
                if (temp != ((String[])mapelement.getValue()).length - 1) {
                    for(int i = temp; i < ((String[])mapelement.getValue()).length; temp = i++) {
                        scenarioText.add(text);
                    }
                } else if (((String[])mapelement.getValue()).length == 1) {
                    scenarioText.add(text);
                }

                String[] value = (String[])mapelement.getValue();

                for(int x = 0; x < value.length; ++x) {
                    if (((String)scenarioText.get(x)).contains((CharSequence)mapelement.getKey())) {
                        String s = ((String)scenarioText.get(x)).replace((CharSequence)mapelement.getKey(), value[x]);
                        scenarioText.set(x, s);
                    }
                }
            }

            if (scenarioText != null) {
                for(int i = 0; i < scenarioText.size(); ++i) {
                    this.scenarioLevelTags.put((String)scenarioText.get(i), tags);
                }
            }
        }

    }

    List<String> getTagsValueList(String str, String prefix) {
        List<String> list = new ArrayList();
        Matcher m = Pattern.compile(prefix).matcher(str);

        while(m.find()) {
            list.add(m.group());
        }

        return list;
    }

    IssueObject convertScenarioToIssue(String scenario, Map<String, String> scenarioLeveltag, Map<String, String> componentLeveltag) {
        Issue issue = new Issue();
        Project project = new Project(String.valueOf(this.prop.getProperty("systemProp.jira.projectName")));
        if (!this.prop.getProperty("systemProp.jira.projectId").equals("")) {
            project.setId(this.prop.getProperty("systemProp.jira.projectId"));
        }

        issue.setProject(project);
        issue.setIssuetype(new IssueType(String.valueOf(this.prop.getProperty("systemProp.jira.issueTypeId"))));
        issue.setFixVersions(Arrays.asList(new Version(this.prop.getProperty("systemProp.jira.version"))));
        String component = (String)componentLeveltag.get(FeatureFileTagsReader.TAGS.COMPONENT.toString());
        Component comp = new Component(component);
        List<Component> components = new ArrayList();
        components.add(comp);
        issue.setComponents(components);
        String subcomponent = (String)componentLeveltag.get(FeatureFileTagsReader.TAGS.SUB_COMPONENT.toString());
        CascadeCustomField cascadeCustomField = new CascadeCustomField(subcomponent);
        issue.setSubComponent(cascadeCustomField);
        String scenarioSummary = scenario.split(":")[1].trim().split("@")[0].trim();
        issue.setSummary(scenarioSummary);
        switch (this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.PRIORITY.toString() + "(\\w+)").split("_")[1].trim()) {
            case "P0":
                issue.setPriority(Priority.P0);
                break;
            case "P1":
                issue.setPriority(Priority.P1);
                break;
            case "P2":
                issue.setPriority(Priority.P2);
                break;
            case "P3":
                issue.setPriority(Priority.P3);
                break;
            default:
                issue.setPriority(Priority.P4);
        }

        String storyId = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.STORY.toString() + "[\\w-]+").split("_")[1].trim();
        String epicId = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.EPIC.toString() + "[\\w-]+").split("_")[1].trim();
        issue.setStoryId(storyId);
        issue.setEpicId(epicId);
        String user = (String)componentLeveltag.get(FeatureFileTagsReader.TAGS.ASSIGNEE.toString());
        String author = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.AUTHOR.toString() + "(\\w+)");
        String assignee = "";
        if (author.isEmpty()) {
            assignee = user;
        } else {
            assignee = author.split("_")[1].trim();
        }

        issue.setAssignee(new User(assignee));
        String testType = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.TYPE.toString() + "(\\w+)").split("_")[1].trim();
        System.out.println(testType);
        switch (testType.toLowerCase()) {
            case "regression":
                issue.setTestTypes(TestType.Regression);
                break;
            case "integration":
                issue.setTestTypes(TestType.Integration);
                break;
            case "smoke":
                issue.setTestTypes(TestType.Smoke);
                break;
            case "performance":
                issue.setTestTypes(TestType.Performance);
                break;
            default:
                issue.setTestTypes(TestType.Sanity);
        }

        testCategory = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.TESTCATEGORY.toString() + "[\\w-]+[::]?").split("_")[1].trim();
        switch (testCategory.toLowerCase()) {
            case "security":
                issue.setTestCategory(TestCategory.Security);
                break;
            case "non-functional":
                issue.setTestCategory(TestCategory.NonFunctional);
                break;
            case "functional":
                issue.setTestCategory(TestCategory.Functional);
                break;
            default:
                issue.setTestCategory(TestCategory.None);
        }

        testSubCategory = this.getTagsValue((String)scenarioLeveltag.get(scenario), FeatureFileTagsReader.TAGS.TESTSUBCATEGORY.toString() + "[\\w-]+[::\\w]*").split("_")[1].replace("::", " ").trim();
        if (testCategory.equalsIgnoreCase("security")) {
            switch (testSubCategory.toLowerCase()) {
                case "container security":
                    issue.setTestSubCategory(TestSubCategory.ContainerSecurity);
                    break;
                case "dynamic analysis":
                    issue.setTestSubCategory(TestSubCategory.DynamicAnalysis);
                    break;
                case "pen testing":
                    issue.setTestSubCategory(TestSubCategory.PENTesting);
                    break;
                case "static analysis":
                    issue.setTestSubCategory(TestSubCategory.StaticAnalysis);
                    break;
                case "tpl checks":
                    issue.setTestSubCategory(TestSubCategory.TPLChecks);
                    break;
                default:
                    issue.setTestSubCategory(TestSubCategory.None);
            }
        } else if (testCategory.equalsIgnoreCase("functional")) {
            switch (testSubCategory.toLowerCase()) {
                case "compatibility":
                    issue.setTestSubCategory(TestSubCategory.Compatibility);
                    break;
                case "contract":
                    issue.setTestSubCategory(TestSubCategory.Contract);
                    break;
                case "functional":
                    issue.setTestSubCategory(TestSubCategory.Functional);
                    break;
                case "globalization":
                    issue.setTestSubCategory(TestSubCategory.Globalization);
                    break;
                case "integration":
                    issue.setTestSubCategory(TestSubCategory.Integration_E2E);
                    break;
                case "pod validation":
                    issue.setTestSubCategory(TestSubCategory.PODValidation);
                    break;
                case "upgrade":
                    issue.setTestSubCategory(TestSubCategory.Upgrade);
                    break;
                case "zdt validation":
                    issue.setTestSubCategory(TestSubCategory.ZDTValidation);
                    break;
                default:
                    issue.setTestSubCategory(TestSubCategory.None);
            }
        } else if (testCategory.equalsIgnoreCase("non-functional")) {
            switch (testSubCategory.toLowerCase()) {
                case "canary":
                    issue.setTestSubCategory(TestSubCategory.Canary);
                    break;
                case "concurreny":
                    issue.setTestSubCategory(TestSubCategory.Concurreny);
                    break;
                case "data destruction":
                    issue.setTestSubCategory(TestSubCategory.DataDestruction);
                    break;
                case "data isolation":
                    issue.setTestSubCategory(TestSubCategory.DataIsolation);
                    break;
                case "ipu validation":
                    issue.setTestSubCategory(TestSubCategory.IPUValidation);
                    break;
                case "license":
                    issue.setTestSubCategory(TestSubCategory.License);
                    break;
                case "noisy neighbour":
                    issue.setTestSubCategory(TestSubCategory.NoisyNeighbour);
                    break;
                case "performance":
                    issue.setTestSubCategory(TestSubCategory.Performance);
                    break;
                case "reliability":
                    issue.setTestSubCategory(TestSubCategory.Reliability);
                    break;
                case "scaling":
                    issue.setTestSubCategory(TestSubCategory.Scaling);
                    break;
                case "service lifecycle":
                    issue.setTestSubCategory(TestSubCategory.ServiceLifecycle);
                    break;
                case "telemetry":
                    issue.setTestSubCategory(TestSubCategory.Telemetry);
                    break;
                case "tenant management":
                    issue.setTestSubCategory(TestSubCategory.TenantManagement);
                    break;
                default:
                    issue.setTestSubCategory(TestSubCategory.None);
            }
        }

        automationStatus = "Automated";
        if (((String)scenarioLeveltag.get(scenario)).contains("@manual")) {
            automationStatus = "Planned";
        }

        switch (automationStatus) {
            case "Planned":
                issue.setAutomationStatuses(AutomationStatus.Planned);
                break;
            case "Automated":
                issue.setAutomationStatuses(AutomationStatus.Automated);
                break;
            default:
                issue.setAutomationStatuses(AutomationStatus.Not_Automatable);
        }

        new ObjectMapper();
        String step = "";
        String testData = "";
        String expResult = "";
        TestStep testStep = new TestStep(step, testData, expResult);
        IssueObject issueObject = new IssueObject();
        issueObject.setTestStep(testStep);
        issueObject.setDescription(scenarioSummary);
        issueObject.setIssue(issue);
        return issueObject;
    }

    public boolean isValidFeatureFile(String file, PrintStream out1) {
        StringBuilder errors = new StringBuilder();
        System.setOut(out1);
        boolean valid = false;
        this.scenarioLevelTags = new LinkedHashMap();
        this.featureLevelTags = new LinkedHashMap();
        this.issues = new LinkedHashMap();
        this.scenarioData = new LinkedHashMap();
        this.scenarioOutlineTags = new LinkedHashMap();
        this.scenarioOutlineTags1 = new LinkedHashMap();
        this.examplesLines = new LinkedHashMap();
        boolean featureTags = false;
        boolean scenariotags = true;
        boolean examplestags = true;
        this.readFile(file);
        this.getAllScenarios();
        if (!this.featureLevelTags.isEmpty()) {
            int msTag = false;
            int moduleTag = false;
            int assingeeTag = false;
            int subCompTag = false;
            int flag = false;
            if (this.featureLevelTags != null) {
                String value;
                if (this.featureLevelTags.containsKey("@MS_")) {
                    value = (String)this.featureLevelTags.get("@MS_");
                    if (value != null) {
                        msTag = true;
                    } else {
                        errors.append("Error! :Value not present for the tag. @MS_ . \n");
                    }
                } else {
                    errors.append("Error! :Tag @MS_ not present for component name in the feature file.Please add the tags in the following format. @MS_{MicroserviceName}\n");
                }

                if (this.featureLevelTags.containsKey("@SubComp_")) {
                    value = (String)this.featureLevelTags.get("@SubComp_");
                    if (value != null) {
                        subCompTag = true;
                    } else {
                        errors.append("Error! :Value not present for the tag. @SubComp .\n ");
                    }
                } else {
                    errors.append("Error! : Tag @SubComp_ not present for sub component name in the feature file.Please add the tags in the following format. \n @SubComp_{SubCommponentname}\n");
                }

                if (this.featureLevelTags.containsKey("@assignee_")) {
                    value = (String)this.featureLevelTags.get("@assignee_");
                    if (value != null) {
                        assingeeTag = true;
                    } else {
                        errors.append("Error! :Value not present for the tag. @assignee_ . \n");
                    }
                } else {
                    errors.append("Error! :Tag @assignee_ not present for assignee name in the feature file.Please add the tags in the following format. \n @assignee_{Your Name} \n");
                }

                if (msTag && assingeeTag && subCompTag) {
                    featureTags = true;
                }
            }
        } else {
            errors.append("Error! :None of the mandatory tags are present at the feature file level.Please add the below tags in the following format. \n @MS_{MicroserviceName} ,@Module_{ModuleName} ,@assignee_{Your Name} \n");
        }

        ArrayList keys;
        Iterator iterator1;
        Map.Entry element;
        String[] k;
        int lineNo;
        if (!this.scenarioOutlineTags1.isEmpty()) {
            keys = new ArrayList();
            iterator1 = this.scenarioOutlineTags1.entrySet().iterator();

            label210:
            while(true) {
                while(true) {
                    do {
                        if (!iterator1.hasNext()) {
                            break label210;
                        }

                        element = (Map.Entry)iterator1.next();
                        k = ((String)element.getKey()).split("\\$");
                        lineNo = Integer.parseInt(k[1]);
                        if (!((String)element.getValue()).contains("PR_")) {
                            errors.append("Error! :PR_ tag not present for the scenario ::" + k[0] + " at line no " + (lineNo - 1) + "\n");
                            scenariotags = false;
                        }

                        if (!((String)element.getValue()).contains("@Story_")) {
                            errors.append("Error! :@Story_ tag not present for the scenario :: " + k[0] + " at line no " + (lineNo - 1) + "\n");
                            errors.append("Please add the tags in the following format @Story_{storyid} \n");
                            scenariotags = false;
                        }

                        if (!((String)element.getValue()).contains("@Epic_")) {
                            errors.append("Error! :@Epic_ tag not present for the scenario :: " + k[0] + " at line no " + (lineNo - 1) + "\n");
                            errors.append("Please add the tags in the following format @Epic_{epicid} \n");
                            scenariotags = false;
                        }

                        if (!((String)element.getValue()).contains("@type_")) {
                            errors.append("Error! :@type_ tag not present for the scenario :: " + k[0] + " at line no " + (lineNo - 1) + "\n");
                            errors.append("Please add the tags in the following format @type_{suitetype} EX:@type_sanity\n");
                            scenariotags = false;
                        }

                        if (!((String)element.getValue()).contains("@testCategory_")) {
                            errors.append("Error! :@testCategory_ tag not present for the scenario :: " + k[0] + " at line no " + (lineNo - 1) + "\n");
                            errors.append("Please add the tags in the following format @type_{suitetype} EX:@type_sanity\n");
                            scenariotags = false;
                        }

                        if (!((String)element.getValue()).contains("@testSubCategory_")) {
                            errors.append("Error! :@testSubCategory_ tag not present for the scenario :: " + k[0] + " at line no " + (lineNo - 1) + "\n");
                            errors.append("Please add the tags in the following format @type_{suitetype} EX:@type_sanity\n");
                            scenariotags = false;
                        }
                    } while(!((String)element.getKey()).contains("Scenario:"));

                    String jiraid;
                    if (((String)element.getKey()).contains("@jiraKey_") && ((String)element.getValue()).contains("@jiraKey_")) {
                        String[] stags = ((String)element.getValue()).trim().split(" ");
                        jiraid = "";

                        int i;
                        for(i = 0; i < stags.length; ++i) {
                            if (stags[i].contains("@jiraKey_")) {
                                jiraid = stags[i];
                                break;
                            }
                        }

                        i = k[0].trim().indexOf("@jiraKey_");
                        String sc = k[0].substring(i);
                        if (!sc.trim().equals(jiraid)) {
                            errors.append("Error! :Jira keys are not same in scenario tags and the scenario line for the scenario :" + k[0] + " at line numbers:" + (lineNo - 1) + " , " + lineNo + "\n");
                            errors.append("Error! :Key at tags :" + jiraid + " Keys at scenario line." + sc + "\n");
                            scenariotags = false;
                        }
                    }

                    int s1;
                    if (((String)element.getValue()).contains("@jiraKey") && !((String)element.getValue()).contains("@jiraKey_X")) {
                        s1 = ((String)element.getValue()).indexOf("@jiraKey");
                        if (s1 != -1) {
                            jiraid = ((String)element.getValue()).substring(s1);
                            if (!keys.contains(jiraid)) {
                                keys.add(jiraid);
                            } else {
                                errors.append("Error! :Duplicate jirakeys found at scenario tags." + (String)element.getValue() + " at line no " + (Integer.parseInt(k[1]) - 1) + "\n");
                                scenariotags = false;
                            }
                        }
                    } else if (!((String)element.getValue()).contains("@jiraKey") && !((String)element.getValue()).contains("@jiraKey_X")) {
                        errors.append("Error! :@jiraKey_X tag not present or place holder value is improper for the scenario at the tag level:: " + k[0] + " at line no " + (Integer.parseInt(k[1]) - 1) + "\n");
                        errors.append("Please add the tags in the following format @jiraKey_X \n");
                        scenariotags = false;
                    }

                    if (k[0].contains("@jiraKey") && !k[0].contains("@jiraKey_X")) {
                        s1 = k[0].indexOf("@jiraKey");
                        if (s1 != -1) {
                            jiraid = k[0].substring(s1);
                            if (!keys.contains(jiraid)) {
                                keys.add(jiraid);
                            } else if (!((String)element.getKey()).contains(jiraid)) {
                                errors.append("Error! :Duplicate jirakeys found at scenario. " + k[0] + " at line no " + Integer.parseInt(k[1]) + "\n");
                                scenariotags = false;
                            }
                        }
                    } else if (!((String)element.getKey()).contains("@jiraKey") && !((String)element.getKey()).contains("@jiraKey_X")) {
                        errors.append("Error! :@jiraKey_X tag not present or place holder value is improper at the scenario line:: " + k[0] + " at line no " + Integer.parseInt(k[1]) + "\n");
                        errors.append("Please add the tags in the following format @jiraKey_X \n");
                        scenariotags = false;
                    }
                }
            }
        }

        if (!this.examplesLines.isEmpty()) {
            keys = new ArrayList();
            iterator1 = this.examplesLines.entrySet().iterator();

            label159:
            while(true) {
                do {
                    do {
                        if (!iterator1.hasNext()) {
                            break label159;
                        }

                        element = (Map.Entry)iterator1.next();
                        if (!((String)element.getValue()).contains("@jiraKey") && !((String)element.getValue()).contains("@jiraKey_X")) {
                            errors.append("Error! :@jiraKey_X tag not present or place holder value is improper at the examples line::" + element.getKey() + "\n");
                            errors.append("Please add the tags in the following format @jiraKey_X\n");
                            examplestags = false;
                        }
                    } while(!((String)element.getValue()).contains("@jiraKey_"));
                } while(((String)element.getValue()).contains("@jiraKey_X"));

                k = ((String)element.getValue()).trim().split("\\|");

                for(lineNo = 0; lineNo < k.length; ++lineNo) {
                    if (k[lineNo].contains("@jiraKey_")) {
                        if (keys.contains(k[lineNo])) {
                            errors.append("Error! :Duplicate jira keys " + k[lineNo] + " found at line no:" + element.getKey() + "\n");
                            examplestags = false;
                        } else {
                            keys.add(k[lineNo]);
                        }
                    }
                }
            }
        }

        if (featureTags && scenariotags && examplestags) {
            valid = true;
        } else {
            valid = false;
        }

        System.out.println("File Name:" + file);
        if (valid) {
            System.out.println("No Validation Errors. \n ");
        } else {
            System.out.println("Validation Errors : \n " + errors.toString().trim() + "\n\n");
        }

        return valid;
    }

    public Map<String, IssueObject> convertfeatureToIssues(String file) {
        this.scenarioLevelTags = new LinkedHashMap();
        this.featureLevelTags = new LinkedHashMap();
        this.issues = new LinkedHashMap();
        this.scenarioData = new LinkedHashMap();
        this.scenarioOutlineTags = new LinkedHashMap();
        this.readFile(file);
        this.getAllScenarios();
        if (!this.scenarioLevelTags.isEmpty() && !this.featureLevelTags.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator1 = this.scenarioLevelTags.entrySet().iterator();

            while(iterator1.hasNext()) {
                Map.Entry<String, String> element = (Map.Entry)iterator1.next();
                String sc = (String)element.getKey();
                if (sc.contains(FeatureFileTagsReader.TAGS.TESTCASEID.toString())) {
                    IssueObject issueObject = this.convertScenarioToIssue(sc, this.scenarioLevelTags, this.featureLevelTags);
                    this.issues.put(issueObject.getIssue().getSummary(), issueObject);
                }
            }
        }

        return this.issues;
    }

    private static enum TAGS {
        COMPONENT("@MS_"),
        SUB_COMPONENT("@SubComp_"),
        PRIORITY("@PR_"),
        TESTCASEID("@jiraKey_X"),
        SCENARIO("Scenario:"),
        EPIC("@Epic_"),
        STORY("@Story_"),
        ASSIGNEE("@assignee_"),
        REPORTER("@reporter_"),
        TYPE("@type_"),
        SCENARIO_OUTLINE("Scenario Outline:"),
        AUTHOR("@author_"),
        TESTCATEGORY("@testCategory_"),
        TESTSUBCATEGORY("@testSubCategory_");

        private String value;

        private TAGS(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}
