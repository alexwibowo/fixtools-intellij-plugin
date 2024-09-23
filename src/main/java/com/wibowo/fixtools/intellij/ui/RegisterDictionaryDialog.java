package com.wibowo.fixtools.intellij.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.Alarm;
import com.intellij.util.containers.ContainerUtil;
import com.wibowo.fixtools.intellij.model.Dictionary;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class RegisterDictionaryDialog extends JDialog implements Disposable {
    private JPanel contentPane;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField aliasTextField;
    private JTextField pathTextField;
    private JButton browseButton;
    private final Alarm myValidationAlarm;

    private Consumer<Dictionary> newDictionaryConsumer;

    public RegisterDictionaryDialog(final Consumer<Dictionary> newDictionaryConsumer) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okButton);

        this.newDictionaryConsumer = newDictionaryConsumer;
        okButton.addActionListener(e -> onOK());
        okButton.setEnabled(false);
        cancelButton.addActionListener(e -> onCancel());
        browseButton.setAction(new BrowseDictionaryAction());
        this.myValidationAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setResizable(false);
        addValidators();
        initValidation();
    }

    /**
     * returns empty list when validation passes
     */
    private List<ValidationInfo> validateAll() {
        final List<ValidationInfo> validationResult = new ArrayList<>();
        runValidationFor(aliasTextField).ifPresent(validationResult::add);
        runValidationFor(pathTextField).ifPresent(validationResult::add);
        return validationResult;
    }

    private Optional<ValidationInfo> runValidationFor(final JTextField component) {

        return ComponentValidator.getInstance(component)
                .flatMap((Function<ComponentValidator, Optional<ValidationInfo>>) componentValidator -> {
                    componentValidator.revalidate();
                    final ValidationInfo validationInfo = componentValidator.getValidationInfo();
                    if (validationInfo != null) {
                        return Optional.of(validationInfo);
                    }
                    return Optional.empty();
                });
    }

    private void addValidators() {
        final ComponentValidator aliasValidator = new ComponentValidator(this).withValidator(() -> {
            final String newAlias = aliasTextField.getText();

            final AppSettings.State state = AppSettings.getInstance().getState();
            if (state != null) {
                final Set<String> dictionaryAliases = state.getDictionaryAliases();
                if (dictionaryAliases.stream().anyMatch(s -> Objects.equals(s, newAlias))) {
                    return new ValidationInfo("That alias has been used", aliasTextField);
                }
            }

            if (StringUtils.isEmpty(newAlias)) {
                return new ValidationInfo("Alias must be provided", aliasTextField);
            } else {
                return null;
            }
        });
        aliasValidator.installOn(aliasTextField);

        // you can trigger validation on document change too
      /*  aliasTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                ComponentValidator.getInstance(aliasTextField).ifPresent(ComponentValidator::revalidate);
            }
        });*/


        final ComponentValidator pathValidator = new ComponentValidator(this).withValidator(() -> {
            if (StringUtils.isEmpty(pathTextField.getText())) {
                return new ValidationInfo("Path to dictionary must be provided", pathTextField);
            } else {
                final File file = new File(pathTextField.getText());
                if (!file.exists()) {
                    return new ValidationInfo("File doesnt exist", pathTextField);
                }
                if (!file.isFile()) {
                    return new ValidationInfo("It is not a file", pathTextField);
                }
                if (!file.canRead()) {
                    return new ValidationInfo("File must be readable", pathTextField);
                }
                return null;
            }
        });
        pathValidator.installOn(pathTextField);
    }

    protected void initValidation() {
        final Runnable validateRequest = () -> {
            final List<ValidationInfo> validationInfos = validateAll();
            if (validationInfos.isEmpty()) {
                okButton.setEnabled(true);
            } else {
                final boolean allValidationResult = ContainerUtil.all(validationInfos, (validationInfo) -> {
                    if (validationInfo != null) {
                        return validationInfo.okEnabled;
                    }
                    return false;
                });
                okButton.setEnabled(allValidationResult);
            }
            this.initValidation();
        };
        this.myValidationAlarm.addRequest(validateRequest, 300);
    }

    private void onOK() {
        // add your code here
        newDictionaryConsumer.accept(new Dictionary(aliasTextField.getText(), pathTextField.getText()));
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private class BrowseDictionaryAction extends AbstractAction {
        public BrowseDictionaryAction() {
            super("Browse...", AllIcons.General.OpenDisk);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(XmlFileType.INSTANCE),
                    null,
                    null,
                    virtualFile -> pathTextField.setText(virtualFile.getPath()));
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("OK");
        panel2.add(okButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel2.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Path");
        panel3.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(29, -1), null, 0, false));
        pathTextField = new JTextField();
        panel3.add(pathTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Alias");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(29, -1), null, 0, false));
        aliasTextField = new JTextField();
        panel3.add(aliasTextField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel3.add(browseButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
