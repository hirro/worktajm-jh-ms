package com.arnellconsulting.worktajm.ms.cucumber.stepdefs;

import com.arnellconsulting.worktajm.ms.WorktajmMsApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = WorktajmMsApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
