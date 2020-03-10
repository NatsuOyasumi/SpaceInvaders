package com.GravlandiaStudios.SpaceInvaders.desktop;

import org.mini2Dx.desktop.DesktopMini2DxConfig;

import com.badlogic.gdx.backends.lwjgl.DesktopMini2DxGame;

import com.GravlandiaStudios.SpaceInvaders.SpaceInvaders;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopMini2DxConfig config = new DesktopMini2DxConfig(SpaceInvaders.GAME_IDENTIFIER);
		config.vSyncEnabled = true;
		new DesktopMini2DxGame(new SpaceInvaders(), config);
	}
}
