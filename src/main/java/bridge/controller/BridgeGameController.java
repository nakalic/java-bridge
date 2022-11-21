package bridge.controller;

import bridge.BridgeGame;
import bridge.BridgeMaker;
import bridge.BridgeRandomNumberGenerator;
import bridge.domain.Bridge;
import bridge.domain.GameStatus;
import bridge.validator.BridgeLengthValidator;
import bridge.validator.RetryInputValidator;
import bridge.validator.SpaceToMoveValidator;
import bridge.view.InputView;
import bridge.view.OutputView;
import camp.nextstep.edu.missionutils.Console;

public class BridgeGameController {

    private InputView inputView;
    private OutputView outputView;
    private BridgeMaker bridgeMaker;
    private BridgeGame bridgeGame;
    private GameStatus gameStatus;
    private Bridge bridge;
    private Bridge copyBridge;

    public BridgeGameController() {
        inputView = new InputView();
        outputView = new OutputView();
        bridgeMaker = new BridgeMaker(new BridgeRandomNumberGenerator());
        bridgeGame = new BridgeGame();
        gameStatus = new GameStatus();
    }

    public void start() {
        makeBridge(getBridgeLength());
        outputView.printStart();
        while (true) {
            boolean check = crossTheBridge();

            String retry = "R";
            outputView.printMap(gameStatus);

            if (!check) {
                retry = getWhetherToRetry();

                if (retry.equals("R")) {
                    bridgeGame.retry(gameStatus);
                    copyBridge = new Bridge(bridge.copyBridge()); // Call by value, Call by reference
                }
            }

            if (retry.equals("Q")) {
                break;
            }

            if (copyBridge.getBridge().size() == 0) {
                gameStatus.gameResult = "성공";
                break;
            }
        }
        outputView.printResult(gameStatus);
    }

    private boolean crossTheBridge() {
        String moving = getSpaceToMove();
        boolean check = bridgeGame.move(gameStatus, copyBridge, moving);
        return check;
    }

    private void makeBridge(int bridgeLength) {
        bridge = new Bridge(bridgeMaker.makeBridge(bridgeLength));
        copyBridge = new Bridge(bridge.copyBridge());
    }

    private String getWhetherToRetry() {
        outputView.askWhetherToRetry();
        while (true) {
            try {
                String input = inputView.readGameCommand();
                RetryInputValidator.validateRetryInput(input);
                return input;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private String getSpaceToMove() {
        outputView.askSpaceToMove();
        while (true) {
            try {
                String input = inputView.readMoving();
                SpaceToMoveValidator.validateSpaceToMove(input);
                return input;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private int getBridgeLength() {
        outputView.askBridgeSize();
        while (true) {
            try {
                String input = Console.readLine();
                BridgeLengthValidator.validateNaturalNumber(input);
                BridgeLengthValidator.validateRange(input);
                return Integer.parseInt(input);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}